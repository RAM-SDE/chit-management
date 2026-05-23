let tableIndex = 0;

// ── Table config ──────────────────────────────────────
function queryParams(params) {
    const limit  = params.limit  || 10;
    const offset = params.offset || 0;
    return {
        search: params.search || '',
        page:   Math.floor(offset / limit),
        size:   limit
    };
}

function responseHandler(res) {
    tableIndex = 0;

    // Group months by receiptNo
    const receiptMap = new Map();
    res.rows.forEach(row => {
        if (!receiptMap.has(row.receiptNo)) {
            receiptMap.set(row.receiptNo, {
                ...row,
                months: [row.monthNumber]
            });
        } else {
            receiptMap.get(row.receiptNo).months.push(row.monthNumber);
        }
    });

    // Attach grouped months + duplicate flag to each row
    const rows = res.rows.map(row => ({
        ...row,
        _allMonths:   receiptMap.get(row.receiptNo).months,
        _isDuplicate: receiptMap.get(row.receiptNo).months[0] !== row.monthNumber
    }));

    return {
        total: res.total,   // ✅ matches server response
        rows:  rows
    };
}

function indexFormatter(value, row, index) {
    return ++tableIndex;
}

function amountFormatter(value) {
    return value
        ? '₹' + Number(value).toLocaleString('en-IN')
        : '—';
}

function statusFormatter(value) {
    const map = {
        'PAID':      'badge bg-success',
        'PENDING':   'badge bg-warning text-dark',
        'PARTIAL':   'badge bg-info',
        'CANCELLED': 'badge bg-danger'
    };
    return `<span class="${map[value] || 'badge bg-secondary'}">${value}</span>`;
}

// ── Action column ─────────────────────────────────────
function actionFormatter(value, row) {
    if (row._isDuplicate) {
        return '—';
    }
    return `
        <button class="btn btn-outline-primary btn-sm"
                data-action="print"
                data-uuid="${row.uuid}"
                title="Print Receipt">
            <i class="fas fa-print"></i>
        </button>`;
}

window.actionEvents = {
    'click [data-action="print"]': function (e, value, row) {
        printReceipt(row);
    }
};

// ── Print only receipt — no navbar/sidebar ────────────
function printReceipt(row) {
    const months = row._allMonths && row._allMonths.length
        ? row._allMonths.join(', ')
        : (row.monthNumber || '—');

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
                .header {
                    text-align: center;
                    margin-bottom: 20px;
                }
                .header h4 {
                    font-size: 20px;
                    font-weight: bold;
                    margin-bottom: 4px;
                }
                .header p {
                    color: #666;
                    font-size: 13px;
                }
                hr {
                    border: none;
                    border-top: 1px solid #ccc;
                    margin: 16px 0;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 8px;
                }
                td {
                    padding: 8px 6px;
                    vertical-align: top;
                    border-bottom: 1px solid #f0f0f0;
                }
                td:first-child {
                    font-weight: bold;
                    width: 45%;
                    color: #555;
                }
                .footer {
                    text-align: center;
                    margin-top: 30px;
                    font-size: 12px;
                    color: #999;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h4>🏦 Chit Management System</h4>
                <p>Payment Receipt</p>
            </div>
            <hr/>
            <table>
                <tr><td>Receipt No</td>    <td>${row.receiptNo    || '—'}</td></tr>
                <tr><td>Customer Name</td> <td>${row.customerName || '—'}</td></tr>
                <tr><td>Phone</td>         <td>${row.customerPhone|| '—'}</td></tr>
                <tr><td>Plan</td>          <td>${row.planName     || '—'}</td></tr>
                <tr><td>Month(s)</td>      <td>${months}</td></tr>
                <tr><td>Amount Paid</td>   <td>₹${(row.amountPaid || 0).toLocaleString('en-IN')}</td></tr>
                <tr><td>Due Amount</td>    <td>₹${(row.dueAmount  || 0).toLocaleString('en-IN')}</td></tr>
                <tr><td>Payment Mode</td>  <td>${row.paymentMode  || '—'}</td></tr>
                <tr><td>Status</td>        <td>${row.status       || '—'}</td></tr>
                <tr><td>Collected By</td>  <td>${row.collectedBy  || '—'}</td></tr>
                <tr><td>Payment Date</td>  <td>${row.paymentDate  || '—'}</td></tr>
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