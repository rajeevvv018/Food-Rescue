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

    // Trigger data fetch for specific pages
    if (pageName === 'claims') {
      if (typeof loadProviderClaims === 'function') {
        loadProviderClaims();
      }
    }
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
      // TODO: Replace with actual backend logout integration (e.g. redirect to LogoutServlet)
      if (confirm('Are you sure you want to logout?')) {
        window.location.href = '../login.html';
      }
    });
  }


  // ===== Add Food Form (Provider) =====
  var addFoodForm = document.getElementById('add-food-form');
  if (addFoodForm) {
    addFoodForm.addEventListener('submit', function (e) {
      // Allow real form submission, just disable button to prevent double submit
      var submitBtn = addFoodForm.querySelector('button[type="submit"]');
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Publishing...';
      }
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
  // ===== Provider Claims API Integration =====
  function loadProviderClaims() {
    var loading = document.getElementById('claims-loading');
    var error = document.getElementById('claims-error');
    var empty = document.getElementById('claims-empty');
    var container = document.getElementById('claims-table-container');
    var tbody = document.getElementById('claims-table-body');

    if (!loading || !error || !empty || !container || !tbody) return;

    // Reset UI states
    loading.classList.remove('hidden');
    error.classList.add('hidden');
    empty.classList.add('hidden');
    container.classList.add('hidden');
    tbody.innerHTML = '';

    fetch('../provider/claims')
      .then(function(response) {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(function(data) {
        loading.classList.add('hidden');

        if (!data || data.length === 0) {
          empty.classList.remove('hidden');
          return;
        }

        container.classList.remove('hidden');

        data.forEach(function(claim) {
          var tr = document.createElement('tr');

          var tdId = document.createElement('td');
          tdId.textContent = claim.claimId || '-';
          tr.appendChild(tdId);

          var tdFood = document.createElement('td');
          var divFood = document.createElement('div');
          divFood.className = 'data-table__food-name';
          divFood.textContent = claim.foodName || 'Unknown Food';
          tdFood.appendChild(divFood);
          tr.appendChild(tdFood);

          var tdNgo = document.createElement('td');
          var ngoName = document.createElement('div');
          ngoName.style.fontWeight = '500';
          ngoName.textContent = claim.ngoName || 'Unknown NGO';
          tdNgo.appendChild(ngoName);

          if (claim.ngoPhone) {
            var ngoPhone = document.createElement('div');
            ngoPhone.style.fontSize = '0.75rem';
            ngoPhone.style.color = 'var(--color-text-muted)';
            ngoPhone.textContent = claim.ngoPhone;
            tdNgo.appendChild(ngoPhone);
          }
          if (claim.ngoEmail) {
            var ngoEmail = document.createElement('div');
            ngoEmail.style.fontSize = '0.75rem';
            ngoEmail.style.color = 'var(--color-text-muted)';
            ngoEmail.textContent = claim.ngoEmail;
            tdNgo.appendChild(ngoEmail);
          }
          tr.appendChild(tdNgo);

          var tdQuantity = document.createElement('td');
          tdQuantity.textContent = claim.claimedQuantity || '0';
          tr.appendChild(tdQuantity);

          var tdUnit = document.createElement('td');
          tdUnit.textContent = claim.unit || '-';
          tr.appendChild(tdUnit);

          var tdStatus = document.createElement('td');
          var badge = document.createElement('span');
          badge.className = 'badge';

          var status = (claim.status || '').toUpperCase();
          badge.textContent = status;

          if (status === 'PENDING') {
            badge.classList.add('badge-warning');
          } else if (status === 'APPROVED' || status === 'COMPLETED' || status === 'PICKED_UP') {
            badge.classList.add('badge-success');
          } else if (status === 'REJECTED' || status === 'CANCELLED') {
            badge.classList.add('badge-danger');
          } else {
            badge.classList.add('badge-info');
          }

          tdStatus.appendChild(badge);
          tr.appendChild(tdStatus);

          var tdDate = document.createElement('td');
          tdDate.textContent = claim.claimedAt ? new Date(claim.claimedAt).toLocaleString() : '-';
          tr.appendChild(tdDate);

          var tdActions = document.createElement('td');
          if (status === 'PENDING') {
            var divActions = document.createElement('div');
            divActions.style.display = 'flex';
            divActions.style.gap = '0.5rem';

            var btnApprove = document.createElement('button');
            btnApprove.className = 'btn btn-sm btn-success';
            btnApprove.textContent = 'Approve';

            var btnReject = document.createElement('button');
            btnReject.className = 'btn btn-sm btn-danger';
            btnReject.textContent = 'Reject';

            divActions.appendChild(btnApprove);
            divActions.appendChild(btnReject);
            tdActions.appendChild(divActions);
          } else {
            tdActions.textContent = '-';
          }
          tr.appendChild(tdActions);

          tbody.appendChild(tr);
        });
      })
      .catch(function(err) {
        loading.classList.add('hidden');
        error.classList.remove('hidden');
        console.error('Error fetching provider claims:', err);
      });
  }

})();
