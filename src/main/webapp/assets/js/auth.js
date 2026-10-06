/**
 * FoodRescue — Auth JavaScript
 * Login & Register form interactions.
 * TODO: Replace mock logic with actual backend API calls.
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

      // Simulate loading
      submitBtn.disabled = true;
      submitBtn.textContent = 'Signing in...';

      // TODO: Replace with actual backend login request
      // Example: fetch('/login', { method: 'POST', body: formData })
      setTimeout(function () {
        // Mock: check for demo credentials
        if (email === 'provider@demo.com') {
          window.location.href = 'provider/dashboard.html';
        } else if (email === 'ngo@demo.com') {
          window.location.href = 'ngo/dashboard.html';
        } else if (email === 'volunteer@demo.com') {
          window.location.href = 'volunteer/dashboard.html';
        } else {
          // For demo, redirect to provider dashboard
          showAlert(alertBox, alertText, 'Demo: Use provider@demo.com, ngo@demo.com, or volunteer@demo.com');
          submitBtn.disabled = false;
          submitBtn.textContent = 'Sign In';
        }
      }, 1000);
    });
  }


  // ===== Register Form =====
  var registerForm = document.getElementById('register-form');
  if (registerForm) {
    registerForm.addEventListener('submit', function (e) {
      e.preventDefault();

      var name = document.getElementById('reg-name').value.trim();
      var email = document.getElementById('reg-email').value.trim();
      var password = document.getElementById('reg-password').value;
      var phone = document.getElementById('reg-phone').value.trim();
      var role = document.getElementById('reg-role').value;
      var address = document.getElementById('reg-address').value.trim();
      var terms = document.getElementById('terms').checked;
      var alertBox = document.getElementById('register-alert');
      var alertText = document.getElementById('register-alert-text');
      var submitBtn = document.getElementById('register-submit');

      // Validation
      if (!name || !email || !password || !phone || !role || !address) {
        showAlert(alertBox, alertText, 'Please fill in all fields.');
        return;
      }

      if (password.length < 8) {
        showAlert(alertBox, alertText, 'Password must be at least 8 characters.');
        return;
      }

      if (!terms) {
        showAlert(alertBox, alertText, 'Please agree to the Terms of Service.');
        return;
      }

      // Simulate loading
      submitBtn.disabled = true;
      submitBtn.textContent = 'Creating account...';

      // TODO: Replace with actual backend register request
      // Example: fetch('/register', { method: 'POST', body: JSON.stringify({...}) })
      setTimeout(function () {
        // Mock success — redirect to login
        submitBtn.textContent = 'Account Created!';
        alertBox.classList.remove('hidden', 'auth-alert--error');
        alertBox.classList.add('auth-alert--success');
        alertText.textContent = 'Account created successfully! Redirecting to login...';

        setTimeout(function () {
          window.location.href = 'login.html';
        }, 1500);
      }, 1200);
    });
  }


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
