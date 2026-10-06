/**
 * FoodRescue — Notifications JavaScript
 * Handles real-time notification polling and display.
 * TODO: Connect to backend notifications endpoint.
 */

(function () {
  'use strict';

  // ===== Mock Notification Data =====
  // TODO: Replace with backend API call
  // Example: fetch('/notifications?unread=true')
  var mockNotifications = [
    {
      id: 1,
      type: 'success',
      title: 'Food Claim Approved',
      message: 'Chicken Biryani claimed by Hope Foundation',
      time: '2 minutes ago',
      read: false
    },
    {
      id: 2,
      type: 'info',
      title: 'Pickup assigned to you',
      message: 'Volunteer Rahul V. assigned for pickup',
      time: '15 minutes ago',
      read: false
    },
    {
      id: 3,
      type: 'warning',
      title: 'New food available nearby',
      message: 'Fresh Cooked Meals from ABC Restaurant',
      time: '1 hour ago',
      read: false
    },
    {
      id: 4,
      type: 'success',
      title: 'Pickup completed',
      message: 'Rice & Dal delivered successfully',
      time: '3 hours ago',
      read: true
    }
  ];


  /**
   * Renders notification items into the panel list.
   * @param {Array} notifications - Array of notification objects
   * @param {HTMLElement} listContainer - The container element for items
   */
  function renderNotifications(notifications, listContainer) {
    if (!listContainer) return;
    listContainer.innerHTML = '';

    var icons = {
      success: '✅',
      warning: '⚠️',
      info: 'ℹ️',
      danger: '❌'
    };

    notifications.forEach(function (notif) {
      var item = document.createElement('div');
      item.className = 'notification-item' + (notif.read ? '' : ' notification-item--unread');
      item.setAttribute('data-id', notif.id);

      item.innerHTML =
        '<div class="notification-item__icon notification-item__icon--' + notif.type + '">' +
          (icons[notif.type] || '🔔') +
        '</div>' +
        '<div class="notification-item__content">' +
          '<div class="notification-item__title">' + notif.title + '</div>' +
          '<div class="notification-item__time">' + notif.time + '</div>' +
        '</div>' +
        (notif.read ? '' : '<div class="notification-item__dot"></div>');

      // Mark individual as read on click
      item.addEventListener('click', function () {
        notif.read = true;
        item.classList.remove('notification-item--unread');
        var dot = item.querySelector('.notification-item__dot');
        if (dot) dot.remove();
        updateUnreadCount(notifications);
      });

      listContainer.appendChild(item);
    });
  }


  /**
   * Updates the unread count badge.
   * @param {Array} notifications
   */
  function updateUnreadCount(notifications) {
    var unread = notifications.filter(function (n) { return !n.read; }).length;
    var countBadge = document.getElementById('notification-count');
    if (countBadge) {
      countBadge.textContent = unread;
      countBadge.style.display = unread > 0 ? 'flex' : 'none';
    }
  }


  /**
   * Initialize notifications if the panel exists on this page.
   */
  function initNotifications() {
    var panel = document.getElementById('notification-panel');
    if (!panel) return;

    var list = panel.querySelector('.notification-panel__list');
    renderNotifications(mockNotifications, list);
    updateUnreadCount(mockNotifications);

    // Mark all read button
    var markAllBtn = document.getElementById('mark-all-read');
    if (markAllBtn) {
      markAllBtn.addEventListener('click', function () {
        mockNotifications.forEach(function (n) { n.read = true; });
        renderNotifications(mockNotifications, list);
        updateUnreadCount(mockNotifications);
      });
    }
  }

  // Run init when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNotifications);
  } else {
    initNotifications();
  }

})();
