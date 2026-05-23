document.addEventListener('DOMContentLoaded', function () {

    if (!PAGE_UUID) {
        alertify.error('Customer not found');
        return;
    }

    loadCustomer();

    function loadCustomer() {
        $.ajax({
            url:  '/api/customers/' + PAGE_UUID,
            type: 'GET',
            success: function (data) {

                document.getElementById('viewName').textContent =
                    data.name || '—';
                document.getElementById('viewPhone').textContent =
                    data.phone || '—';
                document.getElementById('viewEmail').textContent =
                    data.email || '—';
                document.getElementById('viewAadharNo').textContent =
                    data.aadharNo || '—';
                document.getElementById('viewAddress').textContent =
                    data.address || '—';
                document.getElementById('viewCreatedAt').textContent =
                    data.createdAt
                        ? new Date(data.createdAt).toLocaleDateString('en-IN')
                        : '—';

                // Status badge
                document.getElementById('viewStatus').innerHTML = data.active
                    ? '<span class="badge bg-success">Active</span>'
                    : '<span class="badge bg-danger">Inactive</span>';

                // Edit button
                document.getElementById('editBtn').href =
                    '/customers/edit/' + PAGE_UUID;

                // Payment history button
                document.getElementById('paymentHistoryBtn').href =
                    '/payments/history/' + PAGE_UUID;

                // ✅ Toggle button based on active status
                const deactivateBtn = document.getElementById('deactivateBtn');
                const activateBtn   = document.getElementById('activateBtn');

                if (data.active) {
                    deactivateBtn.style.display = 'inline-block';
                    activateBtn.style.display   = 'none';
                } else {
                    deactivateBtn.style.display = 'none';
                    activateBtn.style.display   = 'inline-block';
                }

                document.getElementById('loadingSpinner').style.display = 'none';
                document.getElementById('customerCard').style.display   = 'block';
            },
            error: function () {
                alertify.error('Failed to load customer details');
                document.getElementById('loadingSpinner').style.display = 'none';
            }
        });
    }

    // ── Deactivate ─────────────────────────────
    document.getElementById('deactivateBtn')
        .addEventListener('click', function () {
            Swal.fire({
                title: 'Deactivate Customer?',
                text:  'This customer will be deactivated.',
                icon:  'warning',
                showCancelButton:   true,
                confirmButtonColor: '#d33',
                cancelButtonColor:  '#3085d6',
                confirmButtonText:  'Yes, Deactivate',
                cancelButtonText:   'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url:  '/api/customers/' + PAGE_UUID,
                        type: 'DELETE',
                        success: function (res) {
                            alertify.success(res.message);
                            setTimeout(() => location.reload(), 1500);
                        },
                        error: function (err) {
                            alertify.error(
                                err.responseJSON?.error || 'Something went wrong'
                            );
                        }
                    });
                }
            });
        });

    // ── Activate ───────────────────────────────
    document.getElementById('activateBtn')
        .addEventListener('click', function () {
            Swal.fire({
                title: 'Activate Customer?',
                text:  'This customer will be activated.',
                icon:  'question',
                showCancelButton:   true,
                confirmButtonColor: '#28a745',
                cancelButtonColor:  '#6c757d',
                confirmButtonText:  'Yes, Activate',
                cancelButtonText:   'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url:  '/api/customers/' + PAGE_UUID + '/activate',
                        type: 'PATCH',
                        success: function (res) {
                            alertify.success(res.message);
                            setTimeout(() => location.reload(), 1500);
                        },
                        error: function (err) {
                            alertify.error(
                                err.responseJSON?.error || 'Something went wrong'
                            );
                        }
                    });
                }
            });
        });
});