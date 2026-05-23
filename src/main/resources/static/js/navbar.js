// ── Keep outside DOMContentLoaded — called from HTML onclick ──
function toggleProfile() {
    const pd = document.getElementById('profileDropdown');
    pd.style.display = pd.style.display === 'none' ? 'block' : 'none';
}

// ── DOMContentLoaded ──────────────────────────
document.addEventListener('DOMContentLoaded', function () {

    // ── Set email from JWT cookie ──────────────
    const _email = getEmailFromCookie();
    const profileEmail = document.getElementById('profileEmail');
    if (profileEmail) {
        profileEmail.textContent = _email || 'Unknown';
    }

    // ── Close dropdowns on outside click ──────
    document.addEventListener('click', function (e) {
        const pc   = document.getElementById('profileCircle');
        const pd   = document.getElementById('profileDropdown');
        const bell = document.querySelector('[onclick="toggleNotifications()"]');

        if (pc && pd && !pc.contains(e.target) && !pd.contains(e.target)) {
            pd.style.display = 'none';
        }
    });

    // ── Logout ────────────────────────────────
    alertify.set('notifier', 'position', 'top-right');

    const logoutBtn = document.getElementById('logoutUser');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function (e) {
            e.preventDefault();
            Swal.fire({
                title: 'Are you sure?',
                text: 'You will be logged out!',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Yes, Logout',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: '/api/auth/logout',
                        type: 'POST',
                        success: function (response) {
                            if (response.status) {
                                alertify.success(response.message);
                                setTimeout(function () {
                                    window.location.href = response.redirect;
                                }, 1500);
                            } else {
                                alertify.error(response.message);
                            }
                        },
                        error: function (err) {
                            if (err.responseJSON && err.responseJSON.error) {
                                alertify.error(err.responseJSON.error);
                            } else {
                                alertify.error('Something went wrong. Please try again.');
                            }
                        }
                    });
                }
            });
        });
    }

});