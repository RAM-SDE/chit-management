document.addEventListener('DOMContentLoaded', function () {

    const isEdit = PAGE_UUID !== null;
    const fields = ['planName','totalAmount','durationMonths',
        'totalMembers','startDate'];

    if (isEdit) {
        document.getElementById('formTitle').textContent     = 'Edit Chit Plan';
        document.getElementById('submitBtnText').textContent = 'Update Plan';
        document.getElementById('planUuid').value            = PAGE_UUID;
        loadPlan(PAGE_UUID);
    }

    // ── Auto calculate monthly amount + end date ─
    ['totalAmount','durationMonths','startDate'].forEach(function (id) {
        document.getElementById(id)
            .addEventListener('input', autoCalculate);
    });

    function autoCalculate() {
        const total    = parseFloat(
            document.getElementById('totalAmount').value);
        const duration = parseInt(
            document.getElementById('durationMonths').value);
        const start    = document.getElementById('startDate').value;

        if (total && duration && duration > 0) {
            const monthly = (total / duration).toFixed(2);
            document.getElementById('monthlyAmount').value =
                '₹ ' + Number(monthly).toLocaleString('en-IN');
        } else {
            document.getElementById('monthlyAmount').value = '';
        }

        if (start && duration && duration > 0) {
            const startDate = new Date(start);
            startDate.setMonth(startDate.getMonth() + duration);
            document.getElementById('endDate').value =
                startDate.toISOString().split('T')[0];
        } else {
            document.getElementById('endDate').value = '';
        }
    }

    // ── Load existing plan on edit ─────────────
    function loadPlan(uuid) {
        $.ajax({
            url:  '/api/chit-plans/' + uuid,
            type: 'GET',
            success: function (data) {
                document.getElementById('planName').value =
                    data.planName || '';
                document.getElementById('totalAmount').value =
                    data.totalAmount || '';
                document.getElementById('durationMonths').value =
                    data.durationMonths || '';
                document.getElementById('totalMembers').value =
                    data.totalMembers || '';
                document.getElementById('startDate').value =
                    data.startDate || '';
                autoCalculate();
            },
            error: function () {
                alertify.error('Failed to load plan data');
            }
        });
    }

    // ── Clear errors on input ──────────────────
    fields.forEach(function (id) {
        document.getElementById(id)
            .addEventListener('input', function () {
                document.getElementById(id + 'Error').textContent = '';
                this.classList.remove('is-invalid');
            });
    });

    // ── Form submit ────────────────────────────
    document.getElementById('chitPlanForm')
        .addEventListener('submit', function (e) {
            e.preventDefault();
            clearErrors();

            const uuid = document.getElementById('planUuid').value;
            const data = {
                planName:       document.getElementById('planName').value.trim(),
                totalAmount:    document.getElementById('totalAmount').value,
                durationMonths: document.getElementById('durationMonths').value,
                totalMembers:   document.getElementById('totalMembers').value,
                startDate:      document.getElementById('startDate').value
            };

            const url    = isEdit
                ? '/api/chit-plans/' + uuid
                : '/api/chit-plans';
            const method = isEdit ? 'PUT' : 'POST';

            $.ajax({
                url:         url,
                type:        method,
                contentType: 'application/json',
                data:        JSON.stringify(data),
                success: function (res) {
                    alertify.success(res.message);
                    setTimeout(function () {
                        window.location.href = '/chit-plans';
                    }, 1500);
                },
                error: function (err) {
                    if (err.responseJSON) {
                        const errors = err.responseJSON;
                        if (errors.details) {
                            Object.keys(errors.details)
                                .forEach(function (field) {
                                    const errEl = document.getElementById(
                                        field + 'Error');
                                    const input = document.getElementById(field);
                                    if (errEl) errEl.textContent =
                                        errors.details[field];
                                    if (input) input.classList.add('is-invalid');
                                });
                        }
                        if (errors.error) {
                            alertify.error(errors.error);
                        }
                    } else {
                        alertify.error('Something went wrong. Please try again.');
                    }
                }
            });
        });

    function clearErrors() {
        fields.forEach(function (id) {
            const errEl = document.getElementById(id + 'Error');
            const input = document.getElementById(id);
            if (errEl) errEl.textContent = '';
            if (input) input.classList.remove('is-invalid');
        });
    }
});