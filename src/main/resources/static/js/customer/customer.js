document.addEventListener('DOMContentLoaded', function () {

    const isEdit = PAGE_UUID !== null;

    // ── Update page title and button text ──────
    if (isEdit) {
        document.getElementById('formTitle').textContent    = 'Edit Customer';
        document.getElementById('submitBtnText').textContent = 'Update Customer';
        document.getElementById('customerUuid').value       = PAGE_UUID;
        loadCustomer(PAGE_UUID);
    }

    // ── Load existing data on edit ─────────────
    function loadCustomer(uuid) {
        $.ajax({
            url: '/api/customers/' + uuid,
            type: 'GET',
            success: function (data) {
                document.getElementById('name').value     = data.name     || '';
                document.getElementById('phone').value    = data.phone    || '';
                document.getElementById('email').value    = data.email    || '';
                document.getElementById('aadharNo').value = data.aadharNo || '';
                document.getElementById('address').value  = data.address  || '';
            },
            error: function () {
                alertify.error('Failed to load customer data');
            }
        });
    }

    // ── Clear errors on input ──────────────────
    ['name', 'phone', 'email', 'aadharNo', 'address'].forEach(function (id) {
        document.getElementById(id).addEventListener('input', function () {
            document.getElementById(id + 'Error').textContent = '';
            this.classList.remove('is-invalid');
        });
    });

    // ── Form submit ────────────────────────────
    document.getElementById('customerForm')
        .addEventListener('submit', function (e) {
            e.preventDefault();
            clearErrors();

            const uuid = document.getElementById('customerUuid').value;
            const data = {
                name:     document.getElementById('name').value.trim(),
                phone:    document.getElementById('phone').value.trim(),
                email:    document.getElementById('email').value.trim(),
                aadharNo: document.getElementById('aadharNo').value.trim(),
                address:  document.getElementById('address').value.trim()
            };

            const url    = isEdit ? '/api/customers/' + uuid : '/api/customers';
            const method = isEdit ? 'PUT' : 'POST';

            $.ajax({
                url:         url,
                type:        method,
                contentType: 'application/json',
                data:        JSON.stringify(data),

                success: function (res) {
                    alertify.success(res.message);
                    setTimeout(function () {
                        window.location.href = '/customers';
                    }, 1500);
                },

                error: function (err) {
                    if (err.responseJSON) {
                        const errors = err.responseJSON;
                        // Field validation errors
                        if (errors.details) {
                            Object.keys(errors.details).forEach(function (field) {
                                const errEl = document.getElementById(field + 'Error');
                                const input = document.getElementById(field);
                                if (errEl) errEl.textContent = errors.details[field];
                                if (input) input.classList.add('is-invalid');
                            });
                        }
                        // General error
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
        ['name', 'phone', 'email', 'aadharNo', 'address'].forEach(function (id) {
            document.getElementById(id + 'Error').textContent = '';
            document.getElementById(id).classList.remove('is-invalid');
        });
    }

});