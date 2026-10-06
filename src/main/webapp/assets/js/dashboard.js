/**
 * FoodRescue — Dashboard JavaScript
 * Sidebar navigation, page switching, notification dropdown,
 * mobile sidebar toggle, and shared dashboard interactions.
 * TODO: Replace mock data with backend API responses.
 */

(function () {
  'use strict';

  // ===== Sidebar Page Navigation =====
  var sidebarLinks = document.querySelectorAll('.sidebar__link[data-page]');

  sidebarLinks.forEach(function (link) {
    link.addEventListener('click', function (e) {
      e.preventDefault();
      var page = this.getAttribute('data-page');
      showPage(page);
    });
  });

  // Expose showPage globally for inline onclick handlers
  window.showPage = function (pageName) {
    // Hide all pages
    var pages = document.querySelectorAll('[id^="page-"]');
    pages.forEach(function (p) {
      p.classList.add('hidden');
    });

    // Show target page
    var target = document.getElementById('page-' + pageName);
    if (target) {
      target.classList.remove('hidden');
      // Scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // Update sidebar active state
    sidebarLinks.forEach(function (link) {
      link.classList.remove('sidebar__link--active');
      if (link.getAttribute('data-page') === pageName) {
        link.classList.add('sidebar__link--active');
      }
    });

    // Close mobile sidebar if open
    closeMobileSidebar();
  };


  // ===== Mobile Sidebar Toggle =====
  var sidebarToggle = document.getElementById('sidebar-toggle');
  var sidebar = document.getElementById('sidebar');
  var sidebarOverlay = document.getElementById('sidebar-overlay');

  function openMobileSidebar() {
    if (sidebar) sidebar.classList.add('active');
    if (sidebarOverlay) sidebarOverlay.classList.add('active');
    document.body.style.overflow = 'hidden';
  }

  function closeMobileSidebar() {
    if (sidebar) sidebar.classList.remove('active');
    if (sidebarOverlay) sidebarOverlay.classList.remove('active');
    document.body.style.overflow = '';
  }

  if (sidebarToggle) {
    sidebarToggle.addEventListener('click', openMobileSidebar);
  }

  if (sidebarOverlay) {
    sidebarOverlay.addEventListener('click', closeMobileSidebar);
  }

  // Show/hide sidebar toggle button based on screen size
  function handleSidebarResponsive() {
    if (sidebarToggle) {
      sidebarToggle.style.display = window.innerWidth <= 768 ? 'block' : 'none';
    }
  }

  window.addEventListener('resize', handleSidebarResponsive);
  handleSidebarResponsive();


  // ===== Notification Dropdown =====
  var notifBtn = document.getElementById('notification-btn');
  var notifPanel = document.getElementById('notification-panel');

  if (notifBtn && notifPanel) {
    notifBtn.addEventListener('click', function (e) {
      e.stopPropagation();
      notifPanel.classList.toggle('active');
    });

    // Close when clicking outside
    document.addEventListener('click', function (e) {
      if (!notifPanel.contains(e.target) && !notifBtn.contains(e.target)) {
        notifPanel.classList.remove('active');
      }
    });
  }

  // Mark all read
  var markAllRead = document.getElementById('mark-all-read');
  if (markAllRead) {
    markAllRead.addEventListener('click', function () {
      var unreadItems = document.querySelectorAll('.notification-item--unread');
      unreadItems.forEach(function (item) {
        item.classList.remove('notification-item--unread');
        var dot = item.querySelector('.notification-item__dot');
        if (dot) dot.remove();
      });

      var countBadge = document.getElementById('notification-count');
      if (countBadge) {
        countBadge.textContent = '0';
        countBadge.style.display = 'none';
      }
    });
  }


  // ===== Logout =====
  var logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      // TODO: Replace with actual backend logout request
      // Example: fetch('/logout', { method: 'POST' })
      if (confirm('Are you sure you want to logout?')) {
        window.location.href = '../login.html';
      }
    });
  }


  // ===== Add Food Form (Provider) =====
  var addFoodForm = document.getElementById('add-food-form');
  if (addFoodForm) {
    addFoodForm.addEventListener('submit', function (e) {
      e.preventDefault();

      var submitBtn = addFoodForm.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      submitBtn.textContent = 'Publishing...';

      // TODO: Replace with actual backend API call
      // Example: fetch('/provider/addFood', { method: 'POST', body: new FormData(addFoodForm) })
      setTimeout(function () {
        submitBtn.textContent = 'Published!';
        submitBtn.style.background = 'var(--color-success)';

        // Show toast
        showToast('Food listing published successfully!', 'success');

        setTimeout(function () {
          addFoodForm.reset();
          submitBtn.disabled = false;
          submitBtn.textContent = 'Publish Listing';
          submitBtn.style.background = '';
          showPage('dashboard');
        }, 1500);
      }, 1000);
    });
  }


  // ===== Toast Notification System =====
  window.showToast = function (message, type) {
    type = type || 'success';
    var container = document.querySelector('.toast-container');

    if (!container) {
      container = document.createElement('div');
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    var icons = {
      success: '✅',
      warning: '⚠️',
      danger: '❌',
      info: 'ℹ️'
    };

    var toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.innerHTML =
      '<span class="toast-icon">' + (icons[type] || '✅') + '</span>' +
      '<div class="toast-content">' +
        '<div class="toast-title">' + message + '</div>' +
      '</div>' +
      '<button class="toast-close" onclick="this.parentElement.remove()">✕</button>';

    container.appendChild(toast);

    // Auto remove after 4s
    setTimeout(function () {
      if (toast.parentElement) {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(60px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(function () {
          toast.remove();
        }, 300);
      }
    }, 4000);
  };

})();
