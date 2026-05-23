// 🔥 Remove error instantly when user types

$(document).ready(function(){
    $("#email").on("input", function () {
        if ($(this).val().trim() !== "") {
            $("#emailError").text("");
            $(this).removeClass("error-input").addClass("normal-input");
        }
    });

    $("#password").on("input", function () {
        if ($(this).val().trim() !== "") {
            $("#passwordError").text("");
            $(this).removeClass("error-input").addClass("normal-input");
        }
    });


    // Form Submit
    $("#loginForm").on("submit",function(e){
        e.preventDefault();
        let email = $("#email").val().trim();
        let password = $("#password").val().trim();

        // Clear previous errors
        $("#errorMsg").text("");
        $("#emailError").text("");
        $("#passwordError").text("");

        $("#email").removeClass("error-input").addClass("normal-input");
        $("#password").removeClass("error-input").addClass("normal-input");

        // CSRF
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: $("#loginForm").attr("action"),
            type: "POST",
            headers: {
                [header]: token
            },
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: password
            }),

            success: function (res) {
                alertify.success(res.message);
                $("#loginForm")[0].reset();
                setTimeout(function () {
                    window.location.href = res.redirect;
                }, 1500); // 1500ms = 1.5 seconds
            },

            error: function (err) {
                console.log("error => " + JSON.stringify(err.responseJSON));
                // If backend sends validation errors
                if (err.responseJSON) {

                    let errors = err.responseJSON;

                    // Show email error
                    if (errors.details && errors.details.email) {
                        $("#emailError").text(errors.details.email);
                        $("#email").removeClass("normal-input").addClass("error-input");
                    }
                    if (errors.details && errors.details.password) {
                        $("#passwordError").text(errors.details.password);
                        $("#password").removeClass("normal-input").addClass("error-input");
                    }

                    // General backend message
                    if (errors.error) {
                        alertify.error(errors.error);
                    }

                } else {
                    alertify.error("Something went wrong, Please try again later :)");
                }
            }
        });
    })
})

