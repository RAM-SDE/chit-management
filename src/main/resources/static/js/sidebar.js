// ── Keep outside DOMContentLoaded — called from HTML onclick ──
function toggleMenu(key) {
    const sub  = document.getElementById('sub-' + key);
    const item = sub.previousElementSibling;

    // Close all other open submenus
    document.querySelectorAll('.submenu.open').forEach(el => {
        if (el.id !== 'sub-' + key) {
            el.classList.remove('open');
            el.previousElementSibling.classList.remove('open');
        }
    });

    // Toggle this one
    sub.classList.toggle('open');
    item.classList.toggle('open');
}

function toggleSidebar() {
    const sidebar  = document.getElementById('sidebar');
    const main     = document.getElementById('mainContent');
    const overlay  = document.getElementById('sidebarOverlay');
    const isMobile = window.innerWidth <= 768;

    if (isMobile) {
        sidebar.classList.toggle('mobile-open');
        if (overlay) overlay.classList.toggle('show');
    } else {
        sidebar.classList.toggle('collapsed');
        if (main) main.classList.toggle('expanded');
    }
}

// ── DOMContentLoaded ──────────────────────────
document.addEventListener('DOMContentLoaded', function () {

    // ── Overlay click — close sidebar ─────────
    const overlay = document.getElementById('sidebarOverlay');
    if (overlay) {
        overlay.addEventListener('click', function () {
            const sidebar = document.getElementById('sidebar');
            if (sidebar) sidebar.classList.remove('mobile-open');
            this.classList.remove('show');
        });
    }

    // ── Window resize — reset states ──────────
    window.addEventListener('resize', function () {
        const sidebar = document.getElementById('sidebar');
        const main    = document.getElementById('mainContent');
        const overlay = document.getElementById('sidebarOverlay');

        if (window.innerWidth > 768) {
            if (sidebar) sidebar.classList.remove('mobile-open');
            if (overlay) overlay.classList.remove('show');
        } else {
            if (sidebar) sidebar.classList.remove('collapsed');
            if (main)    main.classList.remove('expanded');
        }
    });

    // ── Mark active menu based on current URL ─
    const currentUrl = window.location.pathname;

    // Check submenu items
    document.querySelectorAll('.submenu-item').forEach(el => {
        if (el.getAttribute('href') === currentUrl) {
            el.classList.add('active');
            const sub = el.closest('.submenu');
            if (sub) {
                sub.classList.add('open');
                sub.previousElementSibling.classList.add('open');
            }
        }
    });

    // Check top level menu items
    document.querySelectorAll('a.menu-item').forEach(el => {
        if (el.getAttribute('href') === currentUrl) {
            el.classList.add('active');
        }
    });

});