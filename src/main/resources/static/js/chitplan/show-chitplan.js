document.addEventListener('DOMContentLoaded', function () {

    if (!PAGE_UUID) {
        alertify.error('Plan not found');
        return;
    }

    loadPlan();

    // ── Load plan data ─────────────────────────
    function loadPlan() {
        $.ajax({
            url:  '/api/chit-plans/' + PAGE_UUID,
            type: 'GET',
            success: function (data) {

                document.getElementById('viewPlanName').textContent =
                    data.planName || '—';
                document.getElementById('viewTotalAmount').textContent =
                    data.totalAmount
                        ? '₹ ' + Number(data.totalAmount)
                        .toLocaleString('en-IN') : '—';
                document.getElementById('viewMonthlyAmount').textContent =
                    data.monthlyAmount
                        ? '₹ ' + Number(data.monthlyAmount)
                        .toLocaleString('en-IN') : '—';
                document.getElementById('viewDuration').textContent =
                    data.durationMonths
                        ? data.durationMonths + ' months' : '—';
                document.getElementById('viewTotalMembers').textContent =
                    data.totalMembers || '—';
                document.getElementById('viewStartDate').textContent =
                    data.startDate || '—';
                document.getElementById('viewEndDate').textContent =
                    data.endDate || '—';
                document.getElementById('viewCreatedBy').textContent =
                    data.createdBy || '—';
                document.getElementById('viewCreatedAt').textContent =
                    data.createdAt
                        ? new Date(data.createdAt)
                            .toLocaleDateString('en-IN') : '—';

                // ── Status badge ───────────────
                const statusMap = {
                    'ACTIVE':    'badge bg-success',
                    'COMPLETED': 'badge bg-info',
                    'CANCELLED': 'badge bg-danger'
                };
                document.getElementById('viewStatus').innerHTML =
                    `<span class="${statusMap[data.status] || 'badge bg-secondary'}">
                        ${data.status}
                    </span>`;

                // ── Edit button ────────────────
                // Show only if ACTIVE and active=true
                const editBtn = document.getElementById('editBtn');
                if (editBtn) {
                    if (data.active && data.status === 'ACTIVE') {
                        editBtn.href = '/chit-plans/edit/' + PAGE_UUID;
                        editBtn.style.display = 'inline-block';
                    } else {
                        editBtn.style.display = 'none';
                    }
                }

                // ── Status buttons ─────────────
                // Complete / Cancel — only when ACTIVE and active=true
                const completeBtn = document.getElementById('completeBtn');
                const cancelBtn   = document.getElementById('cancelBtn');
                if (completeBtn && cancelBtn) {
                    if (data.active && data.status === 'ACTIVE') {
                        completeBtn.style.display = 'inline-block';
                        cancelBtn.style.display   = 'inline-block';
                    } else {
                        completeBtn.style.display = 'none';
                        cancelBtn.style.display   = 'none';
                    }
                }

                // ── Activate / Deactivate ──────
                // Based on active flag only
                const deactivateBtn = document.getElementById('deactivateBtn');
                const activateBtn   = document.getElementById('activateBtn');
                if (deactivateBtn && activateBtn) {
                    if (data.active) {
                        deactivateBtn.style.display = 'inline-block';
                        activateBtn.style.display   = 'none';
                    } else {
                        deactivateBtn.style.display = 'none';
                        activateBtn.style.display   = 'inline-block';
                    }
                }

                document.getElementById('loadingSpinner').style.display = 'none';
                document.getElementById('planCard').style.display       = 'block';
            },
            error: function () {
                alertify.error('Failed to load plan details');
                document.getElementById('loadingSpinner').style.display = 'none';
            }
        });
    }

    // ── Mark Completed ─────────────────────────
    const completeBtn = document.getElementById('completeBtn');
    if (completeBtn) {
        completeBtn.addEventListener('click', function () {
            Swal.fire({
                title: 'Mark as Completed?',
                text:  'This plan will be marked as completed.',
                icon:  'question',
                showCancelButton:   true,
                confirmButtonColor: '#17a2b8',
                cancelButtonColor:  '#6c757d',
                confirmButtonText:  'Yes, Complete',
                cancelButtonText:   'Cancel'
            }).then((result) => {
                if (result.isConfirmed) updateStatus('COMPLETED');
            });
        });
    }

    // ── Cancel Plan ────────────────────────────
    const cancelBtn = document.getElementById('cancelBtn');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function () {
            Swal.fire({
                title: 'Cancel Plan?',
                text:  'All enrollments will be withdrawn and pending payments cancelled.',
                icon:  'warning',
                showCancelButton:   true,
                confirmButtonColor: '#d33',
                cancelButtonColor:  '#6c757d',
                confirmButtonText:  'Yes, Cancel Plan',
                cancelButtonText:   'No'
            }).then((result) => {
                if (result.isConfirmed) updateStatus('CANCELLED');
            });
        });
    }

    // ── Deactivate ─────────────────────────────
    const deactivateBtn = document.getElementById('deactivateBtn');
    if (deactivateBtn) {
        deactivateBtn.addEventListener('click', function () {
            Swal.fire({
                title: 'Deactivate Plan?',
                text:  'All enrollments will be withdrawn and pending payments cancelled.',
                icon:  'warning',
                showCancelButton:   true,
                confirmButtonColor: '#d33',
                cancelButtonColor:  '#6c757d',
                confirmButtonText:  'Yes, Deactivate',
                cancelButtonText:   'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url:  '/api/chit-plans/' + PAGE_UUID,
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
    }

    // ── Activate ───────────────────────────────
    const activateBtn = document.getElementById('activateBtn');
    if (activateBtn) {
        activateBtn.addEventListener('click', function () {
            Swal.fire({
                title: 'Activate Plan?',
                text:  'This plan will be activated.',
                icon:  'question',
                showCancelButton:   true,
                confirmButtonColor: '#28a745',
                cancelButtonColor:  '#6c757d',
                confirmButtonText:  'Yes, Activate',
                cancelButtonText:   'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url:  '/api/chit-plans/' + PAGE_UUID + '/activate',
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
    }

    // ── Update Status helper ───────────────────
    function updateStatus(status) {
        $.ajax({
            url:  '/api/chit-plans/' + PAGE_UUID + '/status?status=' + status,
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