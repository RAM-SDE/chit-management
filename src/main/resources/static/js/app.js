// ════════════════════════════════════════════
// ALERTIFY HELPERS — use these everywhere
// ════════════════════════════════════════════

// Setup alertify defaults once
alertify.set('notifier', 'position', 'top-right');
alertify.set('notifier', 'delay', 4);

// ✅ Success toast
function showSuccess(msg) {
    alertify.success(msg);
}

// ✅ Error toast
function showError(msg) {
    alertify.error(msg);
}

// ✅ Warning toast
function showWarning(msg) {
    alertify.warning(msg);
}

// ✅ Info toast
function showInfo(msg) {
    alertify.message(msg);
}

// ✅ Confirm dialog — replaces confirm()
function showConfirm(msg, onConfirm) {
    alertify.confirm(
        'Confirm',
        msg,
        function() { onConfirm(); },
        function() {} // cancel — do nothing
    ).set('labels', { ok: 'Yes', cancel: 'Cancel' });
}