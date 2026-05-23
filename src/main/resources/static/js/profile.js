// ════════════════════════════════════════════
// PROFILE MODAL
// ════════════════════════════════════════════

function openProfileModal() {
    // Close profile dropdown
    const pd = document.getElementById('profileDropdown');
    if (pd) pd.style.display = 'none';

    // Clear messages
    document.getElementById('profileError')
        .classList.add('d-none');
    document.getElementById('profileSuccess')
        .classList.add('d-none');

    // Load profile data from API
    fetch('/api/profile')
        .then(r => r.json())
        .then(data => {
            if (data.error) {
                console.error('Profile load error:', data.error);
                return;
            }
            fillProfileForm(data);
        })
        .catch(err => console.error('Error:', err));

    // Show modal
    new bootstrap.Modal(
        document.getElementById('profileModal')).show();
}

function fillProfileForm(data) {
    // Editable fields
    document.getElementById('pName').value
        = data.name || '';
    document.getElementById('pPhone').value
        = data.phone || '';
    document.getElementById('pGender').value
        = data.gender || '';
    document.getElementById('pAddress').value
        = data.address || '';

    // Avatar header
    const initial = data.name
        ? data.name.charAt(0).toUpperCase() : '?';
    document.getElementById('profileAvatarLarge')
        .textContent = initial;
    document.getElementById('profileNameDisplay')
        .textContent = data.name || '';
    document.getElementById('profileEmailDisplay')
        .textContent = data.email || '';
    document.getElementById('profileRoleDisplay')
        .innerHTML = data.roleName
        ? `<span class="badge bg-primary">
                   ${data.roleName}</span>` : '';
}

function saveProfile() {
    const errDiv = document.getElementById('profileError');
    const sucDiv = document.getElementById('profileSuccess');
    errDiv.classList.add('d-none');
    sucDiv.classList.add('d-none');

    const name = document.getElementById('pName').value.trim();
    if (!name) {
        errDiv.textContent = 'Name is required';
        errDiv.classList.remove('d-none');
        return;
    }

    const data = {
        name,
        phone:   document.getElementById('pPhone').value.trim(),
        gender:  document.getElementById('pGender').value || null,
        address: document.getElementById('pAddress').value.trim()
    };

    fetch('/api/profile/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(r => r.json())
        .then(res => {
            if (res.error) {
                errDiv.textContent = res.error;
                errDiv.classList.remove('d-none');
                return;
            }
            sucDiv.textContent = '✅ ' + res.message;
            sucDiv.classList.remove('d-none');

            // Update navbar display name
            const initial = data.name.charAt(0).toUpperCase();
            const navInitial = document.getElementById(
                'profileInitial');
            if (navInitial) navInitial.textContent = initial;

            setTimeout(() => {
                bootstrap.Modal.getInstance(
                    document.getElementById('profileModal')).hide();
            }, 1500);
        })
        .catch(() => {
            errDiv.textContent = 'Something went wrong';
            errDiv.classList.remove('d-none');
        });
}

// ════════════════════════════════════════════
// PASSWORD MODAL
// ════════════════════════════════════════════

function openPasswordModal() {
    // Close profile dropdown
    const pd = document.getElementById('profileDropdown');
    if (pd) pd.style.display = 'none';

    // Clear all fields
    document.getElementById('currentPw').value = '';
    document.getElementById('newPw').value = '';
    document.getElementById('confirmPw').value = '';
    document.getElementById('pwError').classList.add('d-none');
    document.getElementById('pwSuccess').classList.add('d-none');
    document.getElementById('pwStrengthBar').style.width = '0%';
    document.getElementById('pwStrengthText').textContent = '';

    // Show modal
    new bootstrap.Modal(
        document.getElementById('passwordModal')).show();

    // Password strength checker
    document.getElementById('newPw')
        .addEventListener('input', checkStrength);
}

function checkStrength() {
    const pw = document.getElementById('newPw').value;
    const bar = document.getElementById('pwStrengthBar');
    const text = document.getElementById('pwStrengthText');

    let strength = 0;
    if (pw.length >= 6)  strength++;
    if (pw.length >= 10) strength++;
    if (/[A-Z]/.test(pw)) strength++;
    if (/[0-9]/.test(pw)) strength++;
    if (/[^A-Za-z0-9]/.test(pw)) strength++;

    const levels = [
        { pct: '20%', cls: 'bg-danger',  label: 'Very Weak' },
        { pct: '40%', cls: 'bg-danger',  label: 'Weak' },
        { pct: '60%', cls: 'bg-warning', label: 'Fair' },
        { pct: '80%', cls: 'bg-info',    label: 'Good' },
        { pct: '100%',cls: 'bg-success', label: 'Strong' }
    ];

    const level = levels[Math.min(strength - 1, 4)] || levels[0];
    bar.style.width = pw.length === 0 ? '0%' : level.pct;
    bar.className   = `progress-bar ${pw.length === 0 ? '' : level.cls}`;
    text.textContent = pw.length === 0 ? '' : level.label;
}

function togglePw(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon  = document.getElementById(iconId);
    if (input.type === 'password') {
        input.type = 'text';
        icon.className = 'fas fa-eye-slash';
    } else {
        input.type = 'password';
        icon.className = 'fas fa-eye';
    }
}

function savePassword() {
    const errDiv = document.getElementById('pwError');
    const sucDiv = document.getElementById('pwSuccess');
    errDiv.classList.add('d-none');
    sucDiv.classList.add('d-none');

    const currentPw = document.getElementById('currentPw').value;
    const newPw     = document.getElementById('newPw').value;
    const confirmPw = document.getElementById('confirmPw').value;

    // Frontend validation
    if (!currentPw || !newPw || !confirmPw) {
        errDiv.textContent = 'All fields are required';
        errDiv.classList.remove('d-none');
        setTimeout(() => errDiv.classList.add('d-none'), 5000);
        return;
    }
    if (newPw.length < 6) {
        errDiv.textContent =
            'New password must be at least 6 characters';
        errDiv.classList.remove('d-none');
        setTimeout(() => errDiv.classList.add('d-none'), 5000);
        return;
    }
    if (newPw !== confirmPw) {
        errDiv.textContent =
            'New password and confirm password do not match';
        errDiv.classList.remove('d-none');
        setTimeout(() => errDiv.classList.add('d-none'), 5000);
        return;
    }
    if (currentPw === newPw) {
        errDiv.textContent =
            'New password must be different from current password';
        errDiv.classList.remove('d-none');
        setTimeout(() => errDiv.classList.add('d-none'), 5000);
        return;
    }

    fetch('/api/profile/password', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            currentPassword: currentPw,
            newPassword:     newPw,
            confirmPassword: confirmPw
        })
    })
        .then(r => r.json())
        .then(res => {
            if (res.error) {
                errDiv.textContent = res.error;
                errDiv.classList.remove('d-none');
                return;
            }
            sucDiv.textContent = '✅ ' + res.message;
            sucDiv.classList.remove('d-none');
            setTimeout(() => errDiv.classList.add('d-none'), 5000);

            // Clear fields
            document.getElementById('currentPw').value = '';
            document.getElementById('newPw').value = '';
            document.getElementById('confirmPw').value = '';

            setTimeout(() => {
                bootstrap.Modal.getInstance(
                    document.getElementById('passwordModal')).hide();
            }, 1500);
        })
        .catch(() => {
            errDiv.textContent = 'Something went wrong';
            errDiv.classList.remove('d-none');
            setTimeout(() => errDiv.classList.add('d-none'), 5000);
        });
}