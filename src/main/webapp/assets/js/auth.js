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
    loginForm.addEventListener('submit', function (e) {
      e.preventDefault();

      var email = document.getElementById('login-email').value.trim();
      var password = document.getElementById('login-password').value;
      var alertBox = document.getElementById('login-alert');
      var alertText = document.getElementById('login-alert-text');
      var submitBtn = document.getElementById('login-submit');

      // Basic validation
      if (!email || !password) {
        showAlert(alertBox, alertText, 'Please fill in all fields.');
        return;
      }

      // Simulate loading state before submission
      submitBtn.disabled = true;
      submitBtn.textContent = 'Signing in...';

      // Let the form submit natively to the Servlet endpoint
      loginForm.submit();
    });
  }


  // ===== Register Form =====
  document.addEventListener("DOMContentLoaded", function () {
    const registerForm = document.getElementById("register-form");

    if (registerForm) {
      registerForm.addEventListener("submit", function (event) {
        if (!registerForm.checkValidity()) {
          event.preventDefault();
          registerForm.reportValidity();
          return;
        }

        const submitButton = document.getElementById("register-submit");

        if (submitButton) {
          submitButton.disabled = true;
          submitButton.textContent = "Creating account...";
        }

        // IMPORTANT:
        // Do NOT call event.preventDefault() here.
        // Browser will naturally POST to:
        // /FoodRescue/register
      });
    }
  });


  // ===== Helper: Show Alert =====
  function showAlert(alertBox, alertText, message) {
    if (!alertBox || !alertText) return;
    alertBox.classList.remove('hidden', 'auth-alert--success');
    alertBox.classList.add('auth-alert--error');
    alertText.textContent = message;

    // Auto hide after 5s
    setTimeout(function () {
      alertBox.classList.add('hidden');
    }, 5000);
  }

})();