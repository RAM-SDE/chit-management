let tableIndex = 0;

function queryParams(params) {
    return {
        limit:  params.limit,
        offset: params.offset,
        search: params.search || ''
    };
}

function responseHandler(res) {
    tableIndex = 0;
    return res;
}

function indexFormatter(value, row, index) {
    return ++tableIndex;
}

function statusFormatter(value) {
    const map = {
        'ACTIVE':    'badge bg-success',
        'WITHDRAWN': 'badge bg-danger',
        'COMPLETED': 'badge bg-info'
    };
    return `<span class="${map[value] || 'badge bg-secondary'}">${value}</span>`;
}

function actionFormatter(value, row) {
    const uuid     = row.uuid;
    const isActive = row.status === 'ACTIVE';

    const withdrawBtn = isActive ? `
        <button class="btn btn-sm btn-outline-danger"
                onclick="confirmWithdraw('${uuid}')"
                title="Withdraw">
            <i class="fas fa-times-circle"></i>
        </button>` : '';

    return `
        <div class="d-flex gap-1">
            ${withdrawBtn}
        </div>`;
}

function confirmWithdraw(uuid) {
    Swal.fire({
        title: 'Withdraw Enrollment?',
        text:  'This customer will be withdrawn from the plan.',
        icon:  'warning',
        showCancelButton:   true,
        confirmButtonColor: '#d33',
        cancelButtonColor:  '#6c757d',
        confirmButtonText:  'Yes, Withdraw',
        cancelButtonText:   'Cancel'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url:  '/api/enrollments/' + uuid,
                type: 'DELETE',
                success: function (res) {
                    alertify.success(res.message);
                    $('#enrollmentTable').bootstrapTable('refresh');
                },
                error: function (err) {
                    alertify.error(
                        err.responseJSON?.error || 'Something went wrong'
                    );
                }
            });
        }
    });
}

// ── Enroll Modal ───────────────────────────────
function openEnrollModal() {
    // Reset form
    document.getElementById('customerSearch').value    = '';
    document.getElementById('selectedCustomerUuid').value = '';
    document.getElementById('selectedCustomerName').textContent = '';
    document.getElementById('customerResults').style.display = 'none';
    document.getElementById('customerUuidError').textContent  = '';
    document.getElementById('chitPlanUuidError').textContent  = '';

    // Load active plans
    loadPlans();

    new bootstrap.Modal(
        document.getElementById('enrollModal')).show();
}

function loadPlans() {
    $.ajax({
        url:  '/api/chit-plans?limit=100&offset=0',
        type: 'GET',
        success: function (res) {
            const select = document.getElementById('chitPlanUuid');
            select.innerHTML = '<option value="">Select a plan</option>';
            if (res.rows) {
                res.rows.forEach(function (plan) {
                    if (plan.active && plan.status === 'ACTIVE') {
                        select.innerHTML +=
                            `<option value="${plan.uuid}">
                                ${plan.planName}
                                (₹${Number(plan.monthlyAmount)
                                .toLocaleString('en-IN')}/month)
                            </option>`;
                    }
                });
            }
        },
        error: function () {
            alertify.error('Failed to load plans');
        }
    });
}

// ── Customer Search ────────────────────────────
let searchTimeout = null;
document.addEventListener('DOMContentLoaded', function () {

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
});

function searchCustomers(keyword) {
    $.ajax({
        url:  '/api/customers?search=' + encodeURIComponent(keyword)
            + '&limit=10&offset=0',
        type: 'GET',
        success: function (res) {
            const results = document.getElementById('customerResults');
            results.innerHTML = '';
            if (res.rows && res.rows.length > 0) {
                res.rows.forEach(function (c) {
                    const item = document.createElement('a');
                    item.href      = 'javascript:void(0)';
                    item.className = 'list-group-item list-group-item-action';
                    item.textContent = c.name + ' — ' + c.phone;
                    item.addEventListener('click', function () {
                        document.getElementById('selectedCustomerUuid').value
                            = c.uuid;
                        document.getElementById('selectedCustomerName')
                            .textContent = '✅ ' + c.name + ' (' + c.phone + ')';
                        document.getElementById('customerSearch').value
                            = c.name;
                        results.style.display = 'none';
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

function submitEnroll() {
    const customerUuid = document.getElementById(
        'selectedCustomerUuid').value;
    const chitPlanUuid = document.getElementById(
        'chitPlanUuid').value;

    // Clear errors
    document.getElementById('customerUuidError').textContent = '';
    document.getElementById('chitPlanUuidError').textContent = '';

    if (!customerUuid) {
        document.getElementById('customerUuidError').textContent
            = 'Please select a customer';
        return;
    }
    if (!chitPlanUuid) {
        document.getElementById('chitPlanUuidError').textContent
            = 'Please select a plan';
        return;
    }

    $.ajax({
        url:         '/api/enrollments',
        type:        'POST',
        contentType: 'application/json',
        data:        JSON.stringify({ customerUuid, chitPlanUuid }),
        success: function (res) {
            alertify.success(res.message);
            bootstrap.Modal.getInstance(
                document.getElementById('enrollModal')).hide();
            $('#enrollmentTable').bootstrapTable('refresh');
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
                alertify.error('Something went wrong. Please try again.');
            }
        }
    });
}