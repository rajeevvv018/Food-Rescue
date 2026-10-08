/**
 * FoodRescue — Notifications JavaScript
 * Handles real-time notification polling and display via API.
 */

(function () {
  'use strict';

  // Base API endpoint
  var API_URL = '../notifications';

  /**
   * Renders notification items into the panel list.
   * @param {Array} notifications - Array of notification objects
   * @param {HTMLElement} listContainer - The container element for items
   */
  function renderNotifications(notifications, listContainer) {
    if (!listContainer) return;
    listContainer.innerHTML = '';

    if (!notifications || notifications.length === 0) {
      listContainer.innerHTML = '<div style="padding:1rem;text-align:center;color:#666;">No notifications yet.</div>';
      return;
    }

    notifications.forEach(function (notif) {
      var item = document.createElement('div');
      item.className = 'notification-item' + (notif.isRead ? '' : ' notification-item--unread');
      item.setAttribute('data-id', notif.id);

      // We'll just use a default bell icon, but you can parse title/message if you want custom icons
      var icon = '🔔';
      if ((notif.title || '').toLowerCase().includes('success') || (notif.title || '').toLowerCase().includes('approved')) icon = '✅';
      else if ((notif.title || '').toLowerCase().includes('reject')) icon = '❌';

      item.innerHTML =
        '<div class="notification-item__icon">' + icon + '</div>' +
        '<div class="notification-item__content">' +
          '<div class="notification-item__title">' + (notif.title || 'Notification') + '</div>' +
          '<div class="notification-item__message" style="font-size:0.8rem;color:#666;margin-bottom:0.25rem;">' + (notif.message || '') + '</div>' +
          '<div class="notification-item__time" style="font-size:0.7rem;color:#999;">' + (notif.createdAt || '') + '</div>' +
        '</div>' +
        (notif.isRead ? '' : '<div class="notification-item__dot"></div>');

      // Mark individual as read on click
      item.addEventListener('click', function () {
        if (notif.isRead) return;

        // API call to mark as read
        var formData = new URLSearchParams();
        formData.append('action', 'read');
        formData.append('notificationId', notif.id);

        fetch(API_URL, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: formData.toString()
        })
        .then(function(res) {
          if (res.ok) {
            notif.isRead = true;
            item.classList.remove('notification-item--unread');
            var dot = item.querySelector('.notification-item__dot');
            if (dot) dot.remove();
            updateUnreadCountBadge();
          }
        })
        .catch(function(err) {
          console.error('Failed to mark notification read', err);
        });
      });

      listContainer.appendChild(item);
    });
  }

  /**
   * Fetches the unread count from API and updates badge.
   */
  function updateUnreadCountBadge() {
    var countBadge = document.getElementById('notification-count');
    if (!countBadge) return;

    fetch(API_URL + '?type=count')
      .then(function(res) {
        if (!res.ok) throw new Error('Failed to fetch count');
        return res.json();
      })
      .then(function(data) {
        if (data && data.success) {
          var unread = data.count || 0;
          countBadge.textContent = unread;
          countBadge.style.display = unread > 0 ? 'inline-flex' : 'none';
        }
      })
      .catch(function(err) {
        console.error('Error fetching notification count:', err);
        countBadge.style.display = 'none';
      });
  }

  /**
   * Fetches all notifications for the dropdown.
   */
  function loadNotifications() {
    var panel = document.getElementById('notification-panel');
    if (!panel) return;
    var list = panel.querySelector('.notification-panel__list');
    if (!list) return;

    list.innerHTML = '<div style="padding:1rem;text-align:center;color:#666;">Loading...</div>';

    fetch(API_URL)
      .then(function(res) {
        if (!res.ok) throw new Error('Failed to fetch notifications');
        return res.json();
      })
      .then(function(data) {
        if (data && data.success) {
          renderNotifications(data.notifications || [], list);
        } else {
          list.innerHTML = '<div style="padding:1rem;text-align:center;color:red;">Error loading notifications.</div>';
        }
      })
      .catch(function(err) {
        console.error('Error fetching notifications:', err);
        list.innerHTML = '<div style="padding:1rem;text-align:center;color:red;">Failed to load notifications.</div>';
      });
  }

  /**
   * Initialize notifications if the panel exists on this page.
   */
  function initNotifications() {
    // Initial fetch of unread count
    updateUnreadCountBadge();

    // Setup Notification Toggle button to load notifications when opened
    var toggleBtn = document.getElementById('notification-btn');
    if (toggleBtn) {
      toggleBtn.addEventListener('click', function() {
        // Load notifications every time it's opened for fresh data
        loadNotifications();
      });
    }

    // Mark all read button
    var markAllBtn = document.getElementById('mark-all-read');
    if (markAllBtn) {
      markAllBtn.addEventListener('click', function () {
        var formData = new URLSearchParams();
        formData.append('action', 'read-all');

        fetch(API_URL, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: formData.toString()
        })
        .then(function(res) {
          if (res.ok) {
            loadNotifications();
            updateUnreadCountBadge();
          }
        })
        .catch(function(err) {
          console.error('Failed to mark all read', err);
        });
      });
    }

    // Poll count every 30 seconds
    setInterval(updateUnreadCountBadge, 30000);
  }

  // Run init when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initNotifications);
  } else {
    initNotifications();
  }

})();
