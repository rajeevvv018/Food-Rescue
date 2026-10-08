/**
 * FoodRescue — Auth JavaScript
 * Login & Register form interactions.
 */

(function () {
  'use strict';

  // ===== Password Toggle =====
  var toggleButtons = document.querySelectorAll('.password-toggle');
  toggleButtons.forEach(function (btn) {
    btn.addEventListener('click', function () {
      var wrapper = btn.closest('.password-wrapper');
      var input = wrapper ? wrapper.querySelector('input') : null;
      if (!input) return;
      if (input.type === 'password') {
        input.type = 'text';
        btn.textContent = '🙈';
      } else {
        input.type = 'password';
        btn.textContent = '👁';
      }
    });
  });


  // ===== Login Form =====
  var loginForm = document.getElementById('login-form');
  if (loginForm) {
    // Check if user just registered successfully
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('registered') === 'true') {
      var alertBox = document.getElementById('login-alert');
      var alertText = document.getElementById('login-alert-text');
      if (alertBox && alertText) {
        alertBox.classList.remove('hidden', 'auth-alert--error');
        alertBox.classList.add('auth-alert--success');
        
        var iconSpan = alertBox.querySelector('span:first-child');
        if (iconSpan) {
          iconSpan.textContent = '✅';
        }
        
        alertText.textContent = 'Account created successfully. Please sign in.';
        // Remove the query param so refresh doesn't show it again
        window.history.replaceState({}, document.title, window.location.pathname);
      }
    }

    loginForm.addEventListener('submit', async function (e) {
      e.preventDefault();

      var email = document.getElementById('login-email').value.trim();
      var password = document.getElementById('login-password').value;
      var alertBox = document.getElementById('login-alert');
      var alertText = document.getElementById('login-alert-text');
      var submitBtn = document.getElementById('login-submit');

      // Basic validation
      if (!email || !password) {
        showAlert(alertBox, alertText, 'Please fill in all fields.', 'error');
        return;
      }

      // Simulate loading state before submission
      submitBtn.disabled = true;
      submitBtn.textContent = 'Signing in...';

      try {
        const formData = new URLSearchParams(new FormData(loginForm));
        const response = await fetch(loginForm.action, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: formData.toString()
        });

        if (response.ok) {
          if (response.redirected) {
            window.location.href = response.url;
          } else {
            // Some fallback if redirect didn't happen properly
            window.location.href = 'index.html';
          }
        } else {
          submitBtn.disabled = false;
          submitBtn.textContent = 'Sign In';

          if (response.status === 401) {
            showAlert(alertBox, alertText, 'Invalid email or password. Please check your credentials and try again.', 'error');
          } else if (response.status === 403) {
            showAlert(alertBox, alertText, 'Access denied. You do not have permission to log in here.', 'error');
          } else {
            showAlert(alertBox, alertText, 'Something went wrong on the server. Please try again later.', 'error');
          }
        }
      } catch (error) {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Sign In';
        showAlert(alertBox, alertText, 'Something went wrong on the server. Please try again later.', 'error');
      }
    });
  }


  // ===== Register Form =====
  const registerForm = document.getElementById("register-form");

  if (registerForm) {
    registerForm.addEventListener("submit", async function (event) {
      event.preventDefault();
      
      if (!registerForm.checkValidity()) {
        registerForm.reportValidity();
        return;
      }

      const submitButton = document.getElementById("register-submit");
      const alertBox = document.getElementById("register-alert");
      const alertText = document.getElementById("register-alert-text");

      if (submitButton) {
        submitButton.disabled = true;
        submitButton.textContent = "Creating account...";
      }

      try {
        const formData = new URLSearchParams(new FormData(registerForm));
        const response = await fetch(registerForm.action, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: formData.toString()
        });

        if (response.ok || response.status === 201) {
          window.location.href = 'login.html?registered=true';
        } else {
          if (submitButton) {
            submitButton.disabled = false;
            submitButton.textContent = "Create Account";
          }
          if (response.status === 409) {
            showAlert(alertBox, alertText, 'An account with this email already exists.', 'error');
          } else if (response.status === 400) {
            showAlert(alertBox, alertText, 'Please ensure all fields are correct.', 'error');
          } else {
            showAlert(alertBox, alertText, 'Something went wrong on the server. Please try again later.', 'error');
          }
        }
      } catch (error) {
        if (submitButton) {
          submitButton.disabled = false;
          submitButton.textContent = "Create Account";
        }
        showAlert(alertBox, alertText, 'Something went wrong on the server. Please try again later.', 'error');
      }
    });
  }


  // ===== Helper: Show Alert =====
  function showAlert(alertBox, alertText, message, type) {
    if (!alertBox || !alertText) return;
    alertBox.classList.remove('hidden', 'auth-alert--success', 'auth-alert--error');
    
    // Attempt to find the icon span to update it
    var iconSpan = alertBox.querySelector('span:first-child');
    if (iconSpan) {
      if (type === 'success') {
        iconSpan.textContent = '✅';
      } else {
        iconSpan.textContent = '⚠️';
      }
    }

    if (type === 'success') {
      alertBox.classList.add('auth-alert--success');
    } else {
      alertBox.classList.add('auth-alert--error');
    }
    
    alertText.textContent = message;

    // Auto hide after 5s if it's an error, or leave it if success? 
    // The prompt says "success message should disappear after a reasonable period".
    setTimeout(function () {
      alertBox.classList.add('hidden');
    }, 5000);
  }

})();