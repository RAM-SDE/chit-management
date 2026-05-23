let selectedEnrollment = null;
let searchTimeout      = null;
let currentReceipt     = null; // ✅ store receipt data

document.addEventListener('DOMContentLoaded', function () {

    // ── Customer search ────────────────────────
    document.getElementById('customerSearch')
        .addEventListener('input', function () {
            clearTimeout(searchTimeout);
            const keyword = this.value.trim();
            if (keyword.length < 2) {
                document.getElementById('customerResults')
                    .style.display = 'none';
                return;
            }
            searchTimeout = setTimeout(function () {
                searchCustomers(keyword);
            }, 300);
        });

    // ── Enrollment change ──────────────────────
    document.getElementById('enrollmentUuid')
        .addEventListener('change', function () {
            const uuid = this.value;
            if (!uuid) {
                document.getElementById('monthSection')
                    .style.display = 'none';
                document.getElementById('paymentDetailsSection')
                    .style.display = 'none';
                return;
            }
            loadPendingMonths(uuid);
        });

    // ── Form submit ────────────────────────────
    document.getElementById('paymentForm')
        .addEventListener('submit', function (e) {
            e.preventDefault();
            submitPayment();
        });
});

// ── Search customers ───────────────────────────
function searchCustomers(keyword) {
    $.ajax({
        url:  '/api/customers?search=' +
            encodeURIComponent(keyword) + '&limit=10&offset=0',
        type: 'GET',
        success: function (res) {
            const results = document.getElementById('customerResults');
            results.innerHTML = '';
            if (res.rows && res.rows.length > 0) {
                res.rows.forEach(function (c) {
                    const item = document.createElement('a');
                    item.href      = 'javascript:void(0)';
                    item.className =
                        'list-group-item list-group-item-action';
                    item.textContent = c.name + ' — ' + c.phone;
                    item.addEventListener('click', function () {
                        document.getElementById('customerSearch').value
                            = c.name;
                        document.getElementById('selectedCustomerName')
                            .textContent =
                            '✅ ' + c.name + ' (' + c.phone + ')';
                        results.style.display = 'none';
                        loadEnrollments(c.uuid);
                    });
                    results.appendChild(item);
                });
                results.style.display = 'block';
            } else {
                results.style.display = 'none';
            }
        }
    });
}

// ── Load enrollments for customer ──────────────
function loadEnrollments(customerUuid) {
    $.ajax({
        url:  '/api/enrollments/customer/' + customerUuid,
        type: 'GET',
        success: function (data) {
            const select = document.getElementById('enrollmentUuid');
            select.innerHTML =
                '<option value="">Select a plan</option>';

            if (data && data.length > 0) {
                data.forEach(function (e) {
                    if (e.status === 'ACTIVE') {
                        select.innerHTML +=
                            `<option value="${e.uuid}">
                                ${e.planName}
                                (₹${Number(e.monthlyAmount)
                                .toLocaleString('en-IN')}/month)
                            </option>`;
                    }
                });
                document.getElementById('enrollmentSection')
                    .style.display = 'block';
                document.getElementById('monthSection')
                    .style.display = 'none';
                document.getElementById('paymentDetailsSection')
                    .style.display = 'none';
            } else {
                alertify.error(
                    'No active enrollments for this customer');
                document.getElementById('enrollmentSection')
                    .style.display = 'none';
            }
        },
        error: function () {
            alertify.error('Failed to load enrollments');
        }
    });
}

// ── Load pending months ────────────────────────
function loadPendingMonths(enrollmentUuid) {
    $.ajax({
        url:  '/api/payments/pending-months/' + enrollmentUuid,
        type: 'GET',
        success: function (res) {
            const container =
                document.getElementById('monthCheckboxes');
            container.innerHTML = '';

            if (res.months && res.months.length > 0) {
                res.months.forEach(function (month) {
                    container.innerHTML += `
                        <div class="form-check form-check-inline">
                            <input class="form-check-input month-check"
                                   type="checkbox"
                                   value="${month}"
                                   id="month${month}"
                                   onchange="updateAmount()">
                            <label class="form-check-label"
                                   for="month${month}">
                                Month ${month}
                            </label>
                        </div>`;
                });
                document.getElementById('monthSection')
                    .style.display = 'block';
                document.getElementById('paymentDetailsSection')
                    .style.display = 'block';

                // Store enrollment info
                const select =
                    document.getElementById('enrollmentUuid');
                const option =
                    select.options[select.selectedIndex];
                const text = option.text;
                const match = text.match(/₹([\d,]+)/);
                selectedEnrollment = {
                    uuid:          enrollmentUuid,
                    monthlyAmount: match
                        ? parseFloat(match[1].replace(/,/g, ''))
                        : 0
                };
                updateAmount();
            } else {
                alertify.success(
                    'All months are paid for this enrollment!');
                document.getElementById('monthSection')
                    .style.display = 'none';
                document.getElementById('paymentDetailsSection')
                    .style.display = 'none';
            }
        },
        error: function () {
            alertify.error('Failed to load pending months');
        }
    });
}

// ── Update amount display ──────────────────────
function updateAmount() {
    const checked = document.querySelectorAll(
        '.month-check:checked').length;
    const total   = checked *
        (selectedEnrollment?.monthlyAmount || 0);
    document.getElementById('amountDisplay').value =
        total > 0
            ? '₹ ' + total.toLocaleString('en-IN')
            : '';
}

// ── Submit payment ─────────────────────────────
function submitPayment() {
    const enrollmentUuid =
        document.getElementById('enrollmentUuid').value;
    const paymentMode =
        document.getElementById('paymentMode').value;
    const remarks =
        document.getElementById('remarks').value.trim();

    const monthNumbers = Array.from(
        document.querySelectorAll('.month-check:checked'))
        .map(cb => parseInt(cb.value));

    // Clear errors
    document.getElementById('enrollmentUuidError').textContent = '';
    document.getElementById('monthNumbersError').textContent   = '';
    document.getElementById('paymentModeError').textContent    = '';

    if (!enrollmentUuid) {
        document.getElementById('enrollmentUuidError').textContent
            = 'Please select a plan';
        return;
    }
    if (monthNumbers.length === 0) {
        document.getElementById('monthNumbersError').textContent
            = 'Please select at least one month';
        return;
    }
    if (!paymentMode) {
        document.getElementById('paymentModeError').textContent
            = 'Please select payment mode';
        return;
    }

    $.ajax({
        url:         '/api/payments',
        type:        'POST',
        contentType: 'application/json',
        data:        JSON.stringify({
            enrollmentUuid,
            monthNumbers,
            paymentMode,
            remarks
        }),
        success: function (res) {
            alertify.success(res.message);
            currentReceipt = res.data; // ✅ store for print
            showReceipt(res.data);
            document.getElementById('paymentForm')
                .style.display = 'none';
        },
        error: function (err) {
            if (err.responseJSON) {
                const errors = err.responseJSON;
                if (errors.field && errors.error) {
                    const errEl = document.getElementById(
                        errors.field + 'Error');
                    if (errEl) errEl.textContent = errors.error;
                } else if (errors.error) {
                    alertify.error(errors.error);
                }
            } else {
                alertify.error('Something went wrong.');
            }
        }
    });
}

// ── Show receipt ───────────────────────────────
function showReceipt(payments) {
    const first   = payments[0];
    const months  = payments.map(p => 'Month ' + p.monthNumber)
        .join(', ');
    const total   = payments.reduce(
        (sum, p) => sum + parseFloat(p.amountPaid), 0);

    document.getElementById('receiptContent').innerHTML = `
        <div class="col-md-6">
            <small class="text-muted">Receipt No</small>
            <p class="fw-bold">${first.receiptNo}</p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Customer</small>
            <p class="fw-bold">${first.customerName}
               (${first.customerPhone})</p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Plan</small>
            <p class="fw-bold">${first.planName}</p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Months Paid</small>
            <p class="fw-bold">${months}</p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Amount Paid</small>
            <p class="fw-bold text-success">
                ₹${total.toLocaleString('en-IN')}
            </p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Payment Mode</small>
            <p class="fw-bold">${first.paymentMode}</p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Collected By</small>
            <p class="fw-bold">${first.collectedBy || '—'}</p>
        </div>
        <div class="col-md-6">
            <small class="text-muted">Date</small>
            <p class="fw-bold">${first.paymentDate || '—'}</p>
        </div>`;

    document.getElementById('receiptCard').style.display = 'block';
    document.getElementById('receiptCard')
        .scrollIntoView({ behavior: 'smooth' });
}

// ── Print receipt — clean window, no navbar ────
function printReceipt() {
    if (!currentReceipt || currentReceipt.length === 0) return;

    const first  = currentReceipt[0];
    const months = currentReceipt
        .map(p => 'Month ' + p.monthNumber).join(', ');
    const total  = currentReceipt
        .reduce((sum, p) => sum + parseFloat(p.amountPaid), 0);

    const html = `
        <html>
        <head>
            <title>Payment Receipt</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body {
                    font-family: Arial, sans-serif;
                    padding: 40px;
                    font-size: 14px;
                    color: #333;
                }
                .header { text-align: center; margin-bottom: 20px; }
                .header h4 {
                    font-size: 20px;
                    font-weight: bold;
                    margin-bottom: 4px;
                }
                .header p { color: #666; font-size: 13px; }
                hr { border: none; border-top: 1px solid #ccc; margin: 16px 0; }
                table { width: 100%; border-collapse: collapse; margin-top: 8px; }
                td { padding: 8px 6px; vertical-align: top; border-bottom: 1px solid #f0f0f0; }
                td:first-child { font-weight: bold; width: 45%; color: #555; }
                .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #999; }
            </style>
        </head>
        <body>
            <div class="header">
                <h4>🏦 Chit Management System</h4>
                <p>Payment Receipt</p>
            </div>
            <hr/>
            <table>
                <tr><td>Receipt No</td>    <td>${first.receiptNo     || '—'}</td></tr>
                <tr><td>Customer Name</td> <td>${first.customerName  || '—'}</td></tr>
                <tr><td>Phone</td>         <td>${first.customerPhone || '—'}</td></tr>
                <tr><td>Plan</td>          <td>${first.planName      || '—'}</td></tr>
                <tr><td>Month(s)</td>      <td>${months}</td></tr>
                <tr><td>Amount Paid</td>   <td>₹${total.toLocaleString('en-IN')}</td></tr>
                <tr><td>Due Amount</td>    <td>₹${(first.dueAmount   || 0).toLocaleString('en-IN')}</td></tr>
                <tr><td>Payment Mode</td>  <td>${first.paymentMode   || '—'}</td></tr>
                <tr><td>Status</td>        <td>${first.status        || '—'}</td></tr>
                <tr><td>Collected By</td>  <td>${first.collectedBy   || '—'}</td></tr>
                <tr><td>Payment Date</td>  <td>${first.paymentDate   || '—'}</td></tr>
            </table>
            <hr/>
            <p class="footer">Thank you for your payment! 🙏</p>
        </body>
        </html>`;

    const win = window.open('', '_blank', 'width=600,height=700');
    win.document.write(html);
    win.document.close();
    win.focus();
    win.print();
    win.onafterprint = () => win.close();
}

// ── Reset form ─────────────────────────────────
function resetForm() {
    currentReceipt = null; // ✅ clear receipt
    document.getElementById('paymentForm').style.display  = 'block';
    document.getElementById('receiptCard').style.display  = 'none';
    document.getElementById('customerSearch').value       = '';
    document.getElementById('selectedCustomerName')
        .textContent = '';
    document.getElementById('enrollmentSection').style.display = 'none';
    document.getElementById('monthSection').style.display      = 'none';
    document.getElementById('paymentDetailsSection')
        .style.display = 'none';
    selectedEnrollment = null;
}