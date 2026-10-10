/**
 * FoodRescue â€” Dashboard JavaScript
 * Sidebar navigation, page switching, notification dropdown,
 * mobile sidebar toggle, and shared dashboard interactions.
 * Handles UI logic for Provider, NGO, and Volunteer dashboards.
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

  // ===== Notifications API Integration =====
  window.fetchNotifications = function() {
    var notifList = document.querySelector('.notification-panel__list');
    var pageNotifContainer = document.getElementById('notifications-container');
    var countBadge = document.getElementById('notification-count');

    // Fetch unread count
    apiFetch('../notifications?action=unreadCount')
      .then(function(res) { return res.json(); })
      .then(function(data) {
        if (data && typeof data.unreadCount === 'number') {
          if (countBadge) {
            countBadge.textContent = data.unreadCount;
            countBadge.style.display = data.unreadCount > 0 ? 'flex' : 'none';
          }
          // Also update sidebar badge if exists
          var sidebarBadge = document.querySelector('.sidebar__link[data-page="notifications"] .sidebar__link-badge');
          if (sidebarBadge) {
            sidebarBadge.textContent = data.unreadCount;
            sidebarBadge.style.display = data.unreadCount > 0 ? 'inline-block' : 'none';
          }
        }
      }).catch(function(e) { console.error('Error fetching unread count', e); });

    // Fetch notifications list
    apiFetch('../notifications')
      .then(function(res) {
        if(!res.ok) throw new Error('Failed to load notifications');
        return res.json();
      })
      .then(function(data) {
        if (notifList) {
          notifList.innerHTML = '';
        }
        var pageHtml = '';
        if (data && data.length > 0) {
          data.forEach(function(n) {
            // Determine Icon based on type
            var icon = 'ðŸ””';
            var iconClass = 'notification-item__icon--info';
            var tstr = (n.title || n.message || '').toUpperCase();
            if (tstr.includes('APPROVED')) { icon = 'âœ…'; iconClass = 'notification-item__icon--success'; }
            else if (tstr.includes('PICKUP') || tstr.includes('VOLUNTEER') || tstr.includes('ACCEPTED')) { icon = 'ðŸš´'; iconClass = 'notification-item__icon--info'; }
            else if (tstr.includes('EXPIRE') || tstr.includes('WARNING') || tstr.includes('REJECTED')) { icon = 'â°'; iconClass = 'notification-item__icon--warning'; }
            else if (tstr.includes('DELIVERED')) { icon = 'ðŸ“¦'; iconClass = 'notification-item__icon--success'; }
            else if (tstr.includes('CLAIM')) { icon = 'âœ‹'; iconClass = 'notification-item__icon--info'; }

            var unreadClass = n.isRead ? '' : 'notification-item--unread';
            var dotHtml = n.isRead ? '' : '<div class="notification-item__dot"></div>';
            var timeHtml = n.createdAt ? '<div class="notification-item__time">' + n.createdAt + '</div>' : '';

            var itemHtml =
              '<div class="notification-item ' + unreadClass + '">' +
                '<div class="notification-item__icon ' + iconClass + '">' + icon + '</div>' +
                '<div class="notification-item__content">' +
                  '<div class="notification-item__title">' + (n.title || n.message) + '</div>' +
                  timeHtml +
                '</div>' +
                dotHtml +
              '</div>';

            if (notifList) {
              notifList.innerHTML += itemHtml;
            }

            // Build larger card for the notifications page
            var cardBorder = n.isRead ? 'border-left: 4px solid var(--color-border);' : 'border-left: 4px solid var(--color-primary);';
            pageHtml +=
              '<div style="padding: 1rem; border-bottom: 1px solid var(--color-border); ' + cardBorder + ' background: ' + (n.isRead ? 'transparent' : 'var(--color-background-alt)') + ';">' +
                '<div style="display: flex; align-items: flex-start; gap: 1rem;">' +
                  '<div style="font-size: 1.5rem;">' + icon + '</div>' +
                  '<div style="flex: 1;">' +
                    '<div style="font-weight: 600; margin-bottom: 0.25rem;">' + (n.title || 'Notification') + '</div>' +
                    '<div style="color: var(--color-text-muted); margin-bottom: 0.5rem; font-size: 0.9rem;">' + n.message + '</div>' +
                    '<div style="color: var(--color-text-muted); font-size: 0.75rem;">' + (n.createdAt || '') + '</div>' +
                  '</div>' +
                '</div>' +
              '</div>';
          });
        } else {
          if (notifList) {
            notifList.innerHTML = '<div style="padding:1rem;text-align:center;color:var(--color-text-muted);">No notifications yet.</div>';
          }
          pageHtml = '<div style="padding:2rem;text-align:center;color:var(--color-text-muted);">No notifications yet.</div>';
        }

        if (pageNotifContainer) {
          pageNotifContainer.innerHTML = pageHtml;
        }
      })
      .catch(function(e) {
        console.error('Error fetching notifications', e);
        if (notifList) {
          notifList.innerHTML = '<div style="padding:1rem;text-align:center;color:red;">Error loading notifications</div>';
        }
        if (pageNotifContainer) {
          pageNotifContainer.innerHTML = '<div style="padding:2rem;text-align:center;color:red;">Unable to load notifications. Please try again.</div>';
        }
      });
  };

  // Mark all read API
  var markAllReadBtn = document.getElementById('mark-all-read');
  if (markAllReadBtn) {
    markAllReadBtn.addEventListener('click', function (e) {
      e.preventDefault();
      apiFetch('../notifications?action=markAllRead', { method: 'POST' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
          if (data && data.success) {
             window.fetchNotifications();
          }
        })
        .catch(function(err) { console.error('Error marking all as read', err); });
    });
  }


  // ===== Logout =====
  var logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      // Redirect to LogoutServlet to invalidate session
      if (confirm('Are you sure you want to logout?')) {
        window.location.href = '../logout';
      }
    });
  }


  // ===== Add Food Form (Provider) =====
  var addFoodForm = document.getElementById('add-food-form');
  if (addFoodForm) {
    addFoodForm.addEventListener('submit', function (e) {
      if (!addFoodForm.checkValidity()) {
        e.preventDefault();
        addFoodForm.reportValidity();
        return;
      }

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
      success: 'âœ…',
      warning: 'âš ï¸',
      danger: 'âŒ',
      info: 'â„¹ï¸'
    };

    var toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.innerHTML =
      '<span class="toast-icon">' + (icons[type] || 'âœ…') + '</span>' +
      '<div class="toast-content">' +
        '<div class="toast-title">' + message + '</div>' +
      '</div>' +
      '<button class="toast-close" onclick="this.parentElement.remove()">âœ•</button>';

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

    apiFetch('../provider/claims')
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
            btnApprove.onclick = function() { handleClaimAction(claim.claimId, 'APPROVE'); };

            var btnReject = document.createElement('button');
            btnReject.className = 'btn btn-sm btn-danger';
            btnReject.textContent = 'Reject';
            btnReject.onclick = function() { handleClaimAction(claim.claimId, 'REJECT'); };

            divActions.appendChild(btnApprove);
            divActions.appendChild(btnReject);
            tdActions.appendChild(divActions);
          } else if (status === 'APPROVED') {
            var btnReady = document.createElement('button');
            btnReady.className = 'btn btn-sm btn-primary';
            btnReady.textContent = 'Ready for Pickup';
            btnReady.onclick = function() { handleClaimAction(claim.claimId, 'READY'); };
            tdActions.appendChild(btnReady);
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

  window.handleClaimAction = function(claimId, action) {
    if (!confirm('Are you sure you want to ' + action.toLowerCase() + ' this claim?')) {
      return;
    }
    var formData = new URLSearchParams();
    formData.append('claimId', claimId);
    formData.append('action', action);

    apiFetch('../provider/claim-action', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData.toString()
    })
    .then(function(response) {
      if (!response.ok) {
        throw new Error('Action failed');
      }
      return response.json();
    })
    .then(function(data) {
      if (typeof showToast === 'function') {
        showToast('Claim updated successfully!', 'success');
      } else {
        alert('Claim updated successfully!');
      }
      loadProviderClaims(); // Refresh claims list
      fetchProviderListings(); // Refresh listings to update active count/status
    })
    .catch(function(err) {
      console.error('Error updating claim:', err);
      if (typeof showToast === 'function') {
        showToast('Failed to update claim.', 'danger');
      } else {
        alert('Failed to update claim.');
      }
    });
  };

  // ===== Fetch Provider Listings =====
  window.providerListingsData = [];

  function fetchProviderListings() {
    var recentTbody = document.getElementById('recent-listings-tbody');
    var allTbody = document.getElementById('all-listings-tbody');
    var activeCountBadge = document.getElementById('active-listings-count');

    if (!recentTbody && !allTbody && !activeCountBadge) return;

    apiFetch('../provider/listings')
      .then(function(response) {
        if (!response.ok) throw new Error('Network error');
        return response.json();
      })
      .then(function(data) {
        window.providerListingsData = data;
        var activeListings = data.filter(function(l) { return l.status === 'AVAILABLE'; });
        if (activeCountBadge) {
          activeCountBadge.textContent = activeListings.length;
        }

        if (recentTbody) {
          var recentRows = '';
          if (data.length === 0) {
            recentRows = '<tr><td colspan="5" style="text-align:center;padding:2rem;">No food listings yet.</td></tr>';
          } else {
            data.slice(0, 3).forEach(function(listing) {
              var statusClass = listing.status === 'AVAILABLE' ? 'badge-success' : (listing.status === 'CLAIMED' ? 'badge-info' : 'badge-neutral');
              var actionHtml = '';
              if (listing.status === 'AVAILABLE') {
                actionHtml += '<div style="display:flex;gap:0.5rem;">';
                actionHtml += '  <button class="btn btn-sm btn-secondary" onclick="editListing(' + listing.id + ')">Edit</button>';
                actionHtml += '</div>';
              } else {
                actionHtml += '<button class="btn btn-sm btn-secondary">Details</button>';
              }

              recentRows += '<tr>';
              recentRows += '  <td>';
              recentRows += '    <div class="data-table__food-info">';
              recentRows += '      <div style="font-size:1.5rem;margin-right:1rem;">ðŸ±</div>';
              recentRows += '      <div>';
              recentRows += '        <div class="data-table__food-name">' + listing.foodName + '</div>';
              recentRows += '        <div class="data-table__food-provider">Added ' + (listing.createdAt || 'recently') + '</div>';
              recentRows += '      </div>';
              recentRows += '    </div>';
              recentRows += '  </td>';
              recentRows += '  <td>' + listing.quantity + ' ' + listing.unit + '</td>';
              recentRows += '  <td>' + listing.expiryTime + '</td>';
              recentRows += '  <td><span class="badge ' + statusClass + '">' + listing.status + '</span></td>';
              recentRows += '  <td>' + actionHtml + '</td>';
              recentRows += '</tr>';
            });
          }
          recentTbody.innerHTML = recentRows;
        }

        if (allTbody) {
          var allRows = '';
          if (data.length === 0) {
            allRows = '<tr><td colspan="6" style="text-align:center;padding:2rem;">No food listings yet.</td></tr>';
          } else {
            data.forEach(function(listing) {
              var statusClass = listing.status === 'AVAILABLE' ? 'badge-success' : (listing.status === 'CLAIMED' ? 'badge-info' : 'badge-neutral');
              var actionHtml = '';
              if (listing.status === 'AVAILABLE') {
                actionHtml += '<div style="display:flex;gap:0.5rem;">';
                actionHtml += '  <button class="btn btn-sm btn-secondary" onclick="editListing(' + listing.id + ')">Edit</button>';
                actionHtml += '  <form action="../provider/cancel-food" method="POST" style="margin:0;" onsubmit="return confirm(\'Are you sure you want to cancel this listing?\');">';
                actionHtml += '    <input type="hidden" name="id" value="' + listing.id + '">';
                actionHtml += '    <button type="submit" class="btn btn-sm btn-danger">Cancel</button>';
                actionHtml += '  </form>';
                actionHtml += '</div>';
              } else {
                actionHtml += '<button class="btn btn-sm btn-secondary">Details</button>';
              }

              allRows += '<tr>';
              allRows += '  <td>';
              allRows += '    <div class="data-table__food-info">';
              allRows += '      <div style="font-size:1.5rem;margin-right:1rem;">ðŸ±</div>';
              allRows += '      <div>';
              allRows += '        <div class="data-table__food-name">' + listing.foodName + '</div>';
              allRows += '      </div>';
              allRows += '    </div>';
              allRows += '  </td>';
              allRows += '  <td>' + listing.quantity + ' ' + listing.unit + '</td>';
              allRows += '  <td>' + (listing.createdAt || 'N/A') + '</td>';
              allRows += '  <td>' + listing.expiryTime + '</td>';
              allRows += '  <td><span class="badge ' + statusClass + '">' + listing.status + '</span></td>';
              allRows += '  <td>' + actionHtml + '</td>';
              allRows += '</tr>';
            });
          }
          allTbody.innerHTML = allRows;
        }
      })
      .catch(function(error) {
        console.error('Error fetching listings:', error);
        if (recentTbody) recentTbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Failed to load listings.</td></tr>';
        if (allTbody) allTbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Failed to load listings.</td></tr>';
      });
  }

  window.editListing = function(id) {
    if (!window.providerListingsData) return;
    var listing = window.providerListingsData.find(function(l) { return l.id === id; });
    if (!listing) return;

    document.getElementById('edit-id').value = listing.id;
    document.getElementById('edit-food-name').value = listing.foodName || '';
    document.getElementById('edit-food-type').value = listing.foodType || '';
    document.getElementById('edit-food-quantity').value = listing.quantity || '';
    document.getElementById('edit-food-unit').value = listing.unit || '';

    var prepDate = new Date();
    if (listing.preparedAt) {
      prepDate = new Date(listing.preparedAt.replace(' ', 'T'));
    }

    if (typeof setUIToJsDate === 'function') {
      setUIToJsDate('edit-prepared', prepDate);

      var expDate = null;
      if (listing.expiryTime) {
        expDate = new Date(listing.expiryTime.replace(' ', 'T'));
      }

      var expiresInSel = document.getElementById('edit-expires-in');
      if (expDate && expiresInSel) {
        var diffMs = expDate.getTime() - prepDate.getTime();
        var diffHours = diffMs / (1000 * 60 * 60);
        var validDurations = [1, 2, 4, 6, 12, 24, 48];

        if (validDurations.indexOf(diffHours) !== -1) {
          expiresInSel.value = diffHours;
          var customContainer = document.getElementById('edit-custom-expiry-container');
          if (customContainer) customContainer.style.display = 'none';
        } else {
          expiresInSel.value = 'custom';
          setUIToJsDate('edit-custom', expDate);
          var customContainer = document.getElementById('edit-custom-expiry-container');
          if (customContainer) customContainer.style.display = 'flex';
        }
      }
      if (typeof updateExpiryPreview === 'function') {
        updateExpiryPreview('edit');
      }
    }

    document.getElementById('edit-food-pickup').value = listing.pickupAddress || '';
    document.getElementById('edit-food-description').value = listing.description || '';

    showPage('edit-food');
  };

  // ===== Provider Pickups Integration =====
  window.fetchProviderPickups = function() {
    var tbody = document.getElementById('provider-pickups-tbody');
    if (!tbody) return;

    apiFetch('../provider/claims')
      .then(function(response) {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(function(data) {
        tbody.innerHTML = '';
        var hasPickups = false;

        data.forEach(function(claim) {
          if (claim.status === 'READY_FOR_PICKUP' || claim.status === 'ACCEPTED' ||
              claim.status === 'PICKED_UP' || claim.status === 'DELIVERED') {
            hasPickups = true;
            var tr = document.createElement('tr');

            var tdFood = document.createElement('td');
            var divFood = document.createElement('div');
            divFood.className = 'data-table__food-name';
            divFood.textContent = claim.foodName || 'Unknown Food';
            tdFood.appendChild(divFood);
            tr.appendChild(tdFood);

            var tdVolunteer = document.createElement('td');
            tdVolunteer.textContent = (claim.status === 'READY_FOR_PICKUP') ? 'Pending Assignment' : 'Assigned';
            tr.appendChild(tdVolunteer);

            var tdNgo = document.createElement('td');
            tdNgo.textContent = claim.ngoName || 'Unknown NGO';
            tr.appendChild(tdNgo);

            var tdStatus = document.createElement('td');
            var badge = document.createElement('span');
            badge.className = 'badge';
            if (claim.status === 'READY_FOR_PICKUP') {
              badge.classList.add('badge-warning');
              badge.textContent = 'Awaiting Volunteer';
            } else if (claim.status === 'ACCEPTED') {
              badge.classList.add('badge-info');
              badge.textContent = 'Volunteer En Route';
            } else if (claim.status === 'PICKED_UP') {
              badge.classList.add('badge-info');
              badge.textContent = 'In Transit';
            } else if (claim.status === 'DELIVERED') {
              badge.classList.add('badge-success');
              badge.textContent = 'Delivered';
            } else {
              badge.classList.add('badge-secondary');
              badge.textContent = claim.status;
            }
            tdStatus.appendChild(badge);
            tr.appendChild(tdStatus);

            tbody.appendChild(tr);
          }
        });

        if (!hasPickups) {
          tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;">No pickup records found.</td></tr>';
        }
      })
      .catch(function(error) {
        console.error('Error fetching provider pickups:', error);
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:red;">Unable to load pickup status. Please try again.</td></tr>';
      });
  };

  // ===== Fetch NGO Available Food =====
  function fetchNGOAvailableFood() {
    var recentAvailableTbody = document.getElementById('ngo-recent-available-tbody');
    var allAvailableTbody = document.getElementById('ngo-all-available-tbody');

    if (!recentAvailableTbody && !allAvailableTbody) return;

    apiFetch('../ngo/available-food')
      .then(function(response) {
        if (!response.ok) throw new Error('Network error');
        return response.json();
      })
      .then(function(data) {
        var renderRows = function(listings) {
          var html = '';
          if (listings.length === 0) {
            html = '<tr><td colspan="6" style="text-align:center;padding:2rem;">No available food found.</td></tr>';
          } else {
            listings.forEach(function(listing) {
              html += '<tr>';
              html += '  <td>';
              html += '    <div class="data-table__food-info">';
              html += '      <div style="font-size:1.5rem;margin-right:1rem;">ðŸ›</div>';
              html += '      <div>';
              html += '        <div class="data-table__food-name">' + listing.foodName + '</div>';
              html += '        <div class="data-table__food-provider">Added ' + (listing.createdAt || 'recently') + '</div>';
              var shortDesc = listing.description ? (listing.description.length > 50 ? listing.description.substring(0, 50) + '...' : listing.description) : 'No description';
              html += '        <div style="font-size:0.85rem;color:#777;margin-top:0.25rem;">' + shortDesc + '</div>';
              html += '      </div>';
              html += '    </div>';
              html += '  </td>';
              html += '  <td>Provider #' + listing.providerId + '</td>';
              html += '  <td>' + listing.quantity + ' ' + listing.unit + '</td>';
              html += '  <td>' + listing.pickupAddress + '</td>';
              html += '  <td><span class="badge badge-warning">' + listing.expiryTime + '</span></td>';
              html += '  <td><button class="btn btn-sm btn-primary" onclick="claimFood(' + listing.id + ', ' + listing.quantity + ')">Claim Food</button></td>';
              html += '</tr>';
            });
          }
          return html;
        };

        if (recentAvailableTbody) {
          recentAvailableTbody.innerHTML = renderRows(data.slice(0, 3));
        }
        if (allAvailableTbody) {
          allAvailableTbody.innerHTML = renderRows(data);
        }
      })
      .catch(function(error) {
        console.error('Error fetching available food:', error);
        var errHtml = '<tr><td colspan="6" style="text-align:center;">Failed to load available food.</td></tr>';
        if (recentAvailableTbody) recentAvailableTbody.innerHTML = errHtml;
        if (allAvailableTbody) allAvailableTbody.innerHTML = errHtml;
      });
  }

  window.claimFood = function(foodId, maxQuantity) {
    var quantity = prompt('Enter quantity to claim (Max: ' + maxQuantity + '):', maxQuantity);
    if (!quantity) return;

    var qNum = parseInt(quantity, 10);
    if (isNaN(qNum) || qNum <= 0 || qNum > maxQuantity) {
      alert('Invalid quantity. Must be between 1 and ' + maxQuantity);
      return;
    }

    // Disable all claim buttons temporarily
    var buttons = document.querySelectorAll('.food-card__actions button');
    buttons.forEach(function(btn) { btn.disabled = true; });

    var params = new URLSearchParams();
    params.append('foodId', foodId);
    params.append('claimedQuantity', qNum);

    apiFetch('../ngo/claim-food', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    })
    .then(function(response) {
      if (response.ok || response.redirected) {
        if (typeof showToast === 'function') {
          showToast('Food claimed successfully! Status: PENDING', 'success');
        } else {
          alert('Food claimed successfully!');
        }
        // Refresh the available food list and claims
        fetchNGOAvailableFood();
        fetchNGOClaims();
        // Switch to claims tab
        showPage('claims');
      } else {
        return response.text().then(function(text) {
          throw new Error('Server returned ' + response.status + ': ' + text);
        });
      }
    })
    .catch(function(error) {
      console.error('Error claiming food:', error);
      alert('Unable to claim food. Please try again or refresh the page.');
    })
    .finally(function() {
      // Re-enable buttons
      var buttons = document.querySelectorAll('.food-card__actions button');
      buttons.forEach(function(btn) { btn.disabled = false; });
    });
  };
  // ===== Fetch NGO Claims =====
  function fetchNGOClaims() {
    var claimsTbody = document.getElementById('ngo-claims-tbody');
    var pickupsTbody = document.getElementById('ngo-pickups-tbody');
    if (!claimsTbody) return;

    apiFetch('../ngo/claims')
      .then(function(response) {
        if (!response.ok) throw new Error('Network error');
        return response.json();
      })
      .then(function(data) {
        window.ngoClaimsData = data;
        var html = '';
        var pickupsHtml = '';
        var activeCount = 0;
        var completedCount = 0;
        var pendingPickupCount = 0;

        if (!data || data.length === 0) {
          html = '<tr><td colspan="5" style="text-align:center;padding:2rem;">No claims found.</td></tr>';
          pickupsHtml = '<tr><td colspan="4" style="text-align:center;padding:2rem;">No pickups found.</td></tr>';
        } else {
          data.forEach(function(claim) {
            var status = (claim.status || '').toUpperCase();

            // Stats logic
            if (status === 'PENDING' || status === 'APPROVED') { activeCount++; }
            else if (status === 'READY_FOR_PICKUP' || status === 'ACCEPTED') { activeCount++; pendingPickupCount++; }
            else if (status === 'PICKED_UP') { pendingPickupCount++; }
            else if (status === 'DELIVERED') { completedCount++; }

            // My Claims Table
            html += '<tr>';
            html += '  <td><div class="data-table__food-name">' + claim.foodName + '</div></td>';
            html += '  <td>' + (claim.providerName || '-') + '</td>';
            html += '  <td>' + claim.claimedQuantity + ' ' + (claim.unit || '') + '</td>';

            var badgeClass = 'badge-info';
            if (status === 'PENDING' || status === 'AWAITING PICKUP') badgeClass = 'badge-warning';
            else if (status === 'APPROVED' || status === 'READY_FOR_PICKUP') badgeClass = 'badge-success';
            else if (status === 'DELIVERED') badgeClass = 'badge-success';
            else if (status === 'REJECTED') badgeClass = 'badge-danger';

            html += '  <td><span class="badge ' + badgeClass + '">' + status + '</span></td>';
            html += '  <td><button class="btn btn-sm btn-secondary" onclick="showClaimDetails(' + claim.claimId + ')">Details</button></td>';
            html += '</tr>';

            // Pickups Table (Only show READY_FOR_PICKUP and beyond)
            if (status === 'READY_FOR_PICKUP' || status === 'ACCEPTED' || status === 'PICKED_UP' || status === 'DELIVERED') {
              pickupsHtml += '<tr>';
              pickupsHtml += '  <td><div class="data-table__food-name">' + claim.foodName + '</div></td>';
              pickupsHtml += '  <td>' + (status === 'READY_FOR_PICKUP' ? '-' : 'Assigned') + '</td>'; // We don't have volunteer info in DTO
              pickupsHtml += '  <td>' + (claim.providerName || '-') + '</td>';
              pickupsHtml += '  <td><span class="badge ' + badgeClass + '">' + status + '</span></td>';
              pickupsHtml += '</tr>';
            }
          });

          if (pickupsHtml === '') {
            pickupsHtml = '<tr><td colspan="4" style="text-align:center;padding:2rem;">No pickups found.</td></tr>';
          }
        }
        claimsTbody.innerHTML = html;
        if (pickupsTbody) pickupsTbody.innerHTML = pickupsHtml;

        // Update stats
        var statActive = document.getElementById('ngo-stat-active-claims');
        var statCompleted = document.getElementById('ngo-stat-completed-claims');
        var statPending = document.getElementById('ngo-stat-pending-pickups');
        if (statActive) statActive.textContent = activeCount;
        if (statCompleted) statCompleted.textContent = completedCount;
        if (statPending) statPending.textContent = pendingPickupCount;
      })
      .catch(function(error) {
        console.error('Error fetching NGO claims:', error);
        claimsTbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:red;">Error loading claims.</td></tr>';
        if (pickupsTbody) pickupsTbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:red;">Error loading pickups.</td></tr>';
      });
  }

  window.showClaimDetails = function(claimId) {
    if (!window.ngoClaimsData) return;
    var claim = window.ngoClaimsData.find(function(c) { return c.claimId === claimId; });
    if (!claim) return;

    var content = document.getElementById('claim-details-content');
    if (!content) return;

    var getRow = function(label, value) {
      return '<div style="display:flex;justify-content:space-between;border-bottom:1px solid #eee;padding-bottom:0.5rem;">' +
             '<strong style="color:#555;">' + label + ':</strong>' +
             '<span style="color:#333;text-align:right;">' + (value || '-') + '</span>' +
             '</div>';
    };

    var html = getRow('Food Name', claim.foodName);
    html += getRow('Description', claim.description);
    html += getRow('Food Type', claim.foodType);
    html += getRow('Claimed Quantity', claim.claimedQuantity + ' ' + (claim.unit || ''));
    html += getRow('Provider', claim.providerName);
    html += getRow('Pickup Address', claim.pickupAddress);
    html += getRow('Prepared At', claim.preparedAt);
    html += getRow('Expiry Time', claim.expiryTime);
    html += getRow('Claim Status', '<span class="badge badge-info">' + claim.status + '</span>');
    html += getRow('Claimed On', claim.claimedAt);

    content.innerHTML = html;

    var modal = new bootstrap.Modal(document.getElementById('claimDetailsModal'));
    modal.show();
  };

  // ===== Volunteer Dashboard Integration =====
  window.fetchVolunteerAvailablePickups = function() {
    var container = document.querySelector('#page-available .dashboard-panel__body');
    if (!container) return; // Not on volunteer dashboard

    apiFetch('../volunteer/available-pickups')
      .then(function(res) {
        if (!res.ok) throw new Error('Network error');
        return res.json();
      })
      .then(function(data) {
        var html = '';
        if (!data || data.length === 0) {
          html = '<div style="padding:2rem;text-align:center;">No available pickups at this moment.</div>';
        } else {
          data.forEach(function(item) {
            html += '<div class="pickup-card">';
            html += '  <div class="pickup-card__header">';
            html += '    <h4 class="pickup-card__title">' + item.foodName + ' &mdash; ' + item.claimedQuantity + ' ' + item.unit + '</h4>';
            html += '    <span class="badge badge-success">Open</span>';
            html += '  </div>';
            html += '  <div class="pickup-card__meta">';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">ðŸª</span><span>' + item.providerName + '</span></div>';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">ðŸ¢</span><span>' + item.ngoName + '</span></div>';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">ðŸ“</span><span>' + item.pickupAddress + '</span></div>';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">â°</span><span>Expires: ' + item.expiryTime + '</span></div>';
            html += '  </div>';
            html += '  <div class="pickup-card__actions">';
            html += '    <button class="btn btn-primary btn-sm" onclick="handlePickupAction(' + item.claimId + ', \'ACCEPT\', this)">Accept Pickup</button>';
            html += '  </div>';
            html += '</div>';
          });
        }
        container.innerHTML = html;

        // Update available badge
        var availableBadge = document.querySelector('.sidebar__link[data-page="available"] .sidebar__link-badge');
        if (availableBadge) {
          availableBadge.textContent = data.length || '';
          availableBadge.style.display = data.length ? 'inline-flex' : 'none';
        }
      })
      .catch(function(err) {
        console.error('Failed to fetch available pickups', err);
        container.innerHTML = '<div style="padding:2rem;text-align:center;color:red;">Error loading pickups.</div>';
      });
  };

  window.fetchVolunteerMyPickups = function() {
    var tbody = document.querySelector('#page-my-pickups tbody');
    var activeContainer = document.querySelector('#page-dashboard .dashboard-panel__body');
    var historyTbody = document.getElementById('volunteer-history-tbody');
    if (!tbody || !activeContainer) return;

    apiFetch('../volunteer/my-pickups')
      .then(function(res) {
        if (!res.ok) throw new Error('Network error');
        return res.json();
      })
      .then(function(data) {
        var tableHtml = '';
        var activeHtml = '';
        var historyHtml = '';
        var activeCount = 0;
        var historyCount = 0;

        if (!data || data.length === 0) {
          tableHtml = '<tr><td colspan="5" style="text-align:center;padding:2rem;">You have no active pickups.</td></tr>';
          activeHtml = '<div style="padding:2rem;text-align:center;">No active pickups.</div>';
          historyHtml = '<tr><td colspan="5" style="text-align:center;padding:2rem;">No pickup history.</td></tr>';
        } else {
          data.forEach(function(pickup) {
            var statusClass = 'badge-info';
            if (pickup.status === 'ACCEPTED') statusClass = 'badge-warning';
            if (pickup.status === 'DELIVERED') statusClass = 'badge-success';

            var trHtml = '<tr>';
            trHtml += '  <td><div class="data-table__food-name">' + pickup.foodName + '</div></td>';
            trHtml += '  <td>' + pickup.providerName + '</td>';
            trHtml += '  <td>' + pickup.ngoName + '</td>';
            trHtml += '  <td>' + (pickup.pickupTime ? pickup.pickupTime.split(' ')[0] : 'N/A') + '</td>';
            trHtml += '  <td><span class="badge ' + statusClass + '">' + pickup.status + '</span></td>';

            var isDelivered = pickup.status === 'DELIVERED';

            if (isDelivered) {
              historyHtml += trHtml + '</tr>';
              historyCount++;
            } else {
              var activeTr = trHtml + '  <td>';
              if (pickup.status === 'ACCEPTED') {
                activeTr += '    <button class="btn btn-sm btn-primary" onclick="handlePickupAction(' + pickup.pickupId + ', \'PICKED_UP\', this)">Mark Picked Up</button>';
              } else if (pickup.status === 'PICKED_UP') {
                activeTr += '    <button class="btn btn-sm btn-primary" onclick="handlePickupAction(' + pickup.pickupId + ', \'DELIVERED\', this)">Mark Delivered</button>';
              }
              activeTr += '  </td></tr>';
              tableHtml += activeTr;
              activeCount++;
            }

            // Active Card View (Only show if not delivered/cancelled)
            if (pickup.status === 'ACCEPTED' || pickup.status === 'PICKED_UP') {
              var isPickedUp = pickup.status === 'PICKED_UP';
              activeHtml += '<div class="pickup-card">';
              activeHtml += '  <div class="pickup-card__header">';
              activeHtml += '    <h4 class="pickup-card__title">' + pickup.foodName + ' &mdash; ' + pickup.quantity + ' ' + pickup.unit + '</h4>';
              activeHtml += '    <span class="badge ' + statusClass + '">' + pickup.status + '</span>';
              activeHtml += '  </div>';

              activeHtml += '  <div class="status-flow">';
              activeHtml += '    <div class="status-flow__step status-flow__step--completed">âœ“ Accepted</div>';
              activeHtml += '    <div class="status-flow__arrow">â†’</div>';
              activeHtml += '    <div class="status-flow__step ' + (isPickedUp ? 'status-flow__step--completed">âœ“' : 'status-flow__step--active">') + ' Picked Up</div>';
              activeHtml += '    <div class="status-flow__arrow">â†’</div>';
              activeHtml += '    <div class="status-flow__step">Delivered</div>';
              activeHtml += '  </div>';

              activeHtml += '  <div class="pickup-card__meta">';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">ðŸª</span><span>' + pickup.providerName + '</span></div>';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">ðŸ¢</span><span>' + pickup.ngoName + '</span></div>';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">ðŸ“</span><span>' + pickup.providerAddress + ' &rarr; ' + pickup.ngoAddress + '</span></div>';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">â°</span><span>Expires: ' + pickup.expiryTime + '</span></div>';
              activeHtml += '  </div>';

              activeHtml += '  <div class="pickup-card__actions">';
              if (!isPickedUp) {
                activeHtml += '    <button class="btn btn-primary btn-sm" onclick="handlePickupAction(' + pickup.pickupId + ', \'PICKED_UP\', this)">Mark Picked Up</button>';
              } else {
                activeHtml += '    <button class="btn btn-primary btn-sm" onclick="handlePickupAction(' + pickup.pickupId + ', \'DELIVERED\', this)">Mark Delivered</button>';
              }
              activeHtml += '  </div>';
              activeHtml += '</div>';
            }
          });

          if (activeCount === 0) {
            tableHtml = '<tr><td colspan="5" style="text-align:center;padding:2rem;">You have no active pickups.</td></tr>';
            activeHtml = '<div style="padding:2rem;text-align:center;">No active pickups.</div>';
          }
          if (historyCount === 0) {
            historyHtml = '<tr><td colspan="5" style="text-align:center;padding:2rem;">No pickup history.</td></tr>';
          }
        }

        tbody.innerHTML = tableHtml;
        activeContainer.innerHTML = activeHtml;
        if (historyTbody) {
          historyTbody.innerHTML = historyHtml;
        }
      })
      .catch(function(err) {
        console.error('Failed to fetch my pickups', err);
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:red;">Unable to load data. Please try again.</td></tr>';
        activeContainer.innerHTML = '<div style="padding:2rem;text-align:center;color:red;">Unable to load data. Please try again.</div>';
        if (historyTbody) {
          historyTbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:red;">Unable to load data. Please try again.</td></tr>';
        }
      });
  };

  window.handlePickupAction = function(id, action, btnElem) {
    if (!confirm('Are you sure you want to perform this action (' + action + ')?')) {
      return;
    }

    // Prevent double submission
    if (btnElem) {
      btnElem.disabled = true;
      btnElem.textContent = 'Processing...';
    }

    var formData = new URLSearchParams();
    formData.append('action', action);
    if (action === 'ACCEPT') {
      formData.append('claimId', id);
    } else {
      formData.append('pickupId', id);
    }

    apiFetch('../volunteer/pickup-action', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: formData.toString()
    })
    .then(function(res) {
      if (!res.ok) throw new Error('Action failed');
      return res.json();
    })
    .then(function(data) {
      if (typeof showToast === 'function') {
        showToast(data.message || 'Action completed successfully!', 'success');
      } else {
        alert(data.message || 'Action completed successfully!');
      }
      // Refresh UI
      if (typeof fetchVolunteerAvailablePickups === 'function') fetchVolunteerAvailablePickups();
      if (typeof fetchVolunteerMyPickups === 'function') fetchVolunteerMyPickups();
    })
    .catch(function(err) {
      console.error('Action error:', err);
      if (typeof showToast === 'function') {
        showToast('Action failed. Invalid state or unauthorized.', 'danger');
      } else {
        alert('Action failed.');
      }
      if (btnElem) {
        btnElem.disabled = false;
        btnElem.textContent = action.replace('_', ' ');
      }
    });
  };

  // ===== Profile Handling =====
  window.fetchUserProfile = function() {
    apiFetch('../profile')
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch profile');
        return res.json();
      })
      .then(user => {
        // Update header
        const headerName = document.getElementById('header-user-name');
        const headerInitials = document.getElementById('header-avatar-initials');
        if (headerName) headerName.textContent = user.name;
        if (headerInitials) {
          const names = user.name.split(' ');
          headerInitials.textContent = names.length > 1 ? (names[0][0] + names[1][0]).toUpperCase() : user.name.substring(0, 2).toUpperCase();
        }

        // Update sidebar
        const sidebarName = document.getElementById('sidebar-user-name');
        const sidebarEmail = document.getElementById('sidebar-user-email');
        if (sidebarName) sidebarName.textContent = user.name;
        if (sidebarEmail) sidebarEmail.textContent = user.email;

        // Update main dashboard welcome
        const welcome = document.getElementById('dashboard-welcome');
        if (welcome) welcome.textContent = 'Welcome back, ' + user.name.split(' ')[0] + '! Here\'s your overview.';

        // Update profile page
        const profileAvatar = document.getElementById('profile-card-avatar');
        const profileName = document.getElementById('profile-card-name');
        if (profileAvatar && headerInitials) profileAvatar.textContent = headerInitials.textContent;
        if (profileName) profileName.textContent = user.name;

        const pName = document.getElementById('profile-field-name');
        const pEmail = document.getElementById('profile-field-email');
        const pPhone = document.getElementById('profile-field-phone');
        const pRole = document.getElementById('profile-field-role');
        const pAddress = document.getElementById('profile-field-address');

        if (pName) pName.textContent = user.name;
        if (pEmail) pEmail.textContent = user.email;
        if (pPhone) pPhone.textContent = user.phone || 'Not provided';

        // Make role more readable if necessary
        let displayRole = user.role;
        if (user.role === 'PROVIDER') displayRole = 'Food Provider';
        else if (user.role === 'NGO') displayRole = 'NGO / Organization';
        else if (user.role === 'VOLUNTEER') displayRole = 'Volunteer';

        if (pRole) pRole.textContent = displayRole;
        const roleBadge = document.getElementById('profile-card-role-badge');
        if (roleBadge) roleBadge.textContent = displayRole;

        if (pAddress) pAddress.textContent = user.address || 'Not provided';

        // Store user globally for editing
        window.currentUserProfile = user;
      })
      .catch(err => {
        console.error('Error fetching profile:', err);
        const headerName = document.getElementById('header-user-name');
        if (headerName) headerName.textContent = 'Error loading profile';
        if (typeof showToast === 'function') {
          showToast('Unable to load profile data. Please refresh.', 'danger');
        }
      });
  };

  window.openEditProfileModal = function() {
    if (!window.currentUserProfile) return;
    const user = window.currentUserProfile;
    document.getElementById('edit-profile-name').value = user.name;
    document.getElementById('edit-profile-email').value = user.email;
    document.getElementById('edit-profile-phone').value = user.phone || '';
    document.getElementById('edit-profile-address').value = user.address || '';

    const alertBox = document.getElementById('edit-profile-alert');
    if (alertBox) alertBox.classList.add('d-none');

    const modal = new bootstrap.Modal(document.getElementById('editProfileModal'));
    modal.show();
  };

  const editProfileForm = document.getElementById('edit-profile-form');
  if (editProfileForm) {
    editProfileForm.addEventListener('submit', function(e) {
      e.preventDefault();
      const submitBtn = document.getElementById('edit-profile-submit');
      submitBtn.disabled = true;
      submitBtn.textContent = 'Saving...';

      const formData = new URLSearchParams(new FormData(editProfileForm));

      apiFetch('../profile', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
      })
      .then(res => {
        if (!res.ok) throw new Error('Update failed');
        return res.json();
      })
      .then(data => {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Save Changes';

        const modal = bootstrap.Modal.getInstance(document.getElementById('editProfileModal'));
        if (modal) modal.hide();

        if (typeof showToast === 'function') {
          showToast('Profile updated successfully!', 'success');
        } else {
          alert('Profile updated successfully!');
        }
        // Refresh profile data on UI
        fetchUserProfile();
      })
      .catch(err => {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Save Changes';
        const alertBox = document.getElementById('edit-profile-alert');
        if (alertBox) {
          alertBox.textContent = 'Failed to update profile. Please try again.';
          alertBox.className = 'alert alert-danger';
          alertBox.classList.remove('d-none');
        }
      });
    });
  }

  // ===== 12-Hour DateTime UI Logic =====
  function padZero(n) { return n < 10 ? '0' + n : '' + n; }

  window.populateTimeDropdowns = function(prefix) {
    var hourSelect = document.getElementById(prefix + '-hour');
    var minSelect = document.getElementById(prefix + '-minute');
    if (!hourSelect || !minSelect) return;

    hourSelect.innerHTML = '';
    for (var i = 1; i <= 12; i++) {
      var val = padZero(i);
      hourSelect.add(new Option(val, val));
    }

    minSelect.innerHTML = '';
    for (var i = 0; i < 60; i += 1) {
      var val = padZero(i);
      minSelect.add(new Option(val, val));
    }
  };

  window.getBackendFormat = function(dateStr, hourStr, minStr, ampmStr) {
    if (!dateStr || !hourStr || !minStr || !ampmStr) return '';
    var h = parseInt(hourStr, 10);
    if (ampmStr === 'PM' && h !== 12) h += 12;
    if (ampmStr === 'AM' && h === 12) h = 0;
    return dateStr + 'T' + padZero(h) + ':' + minStr;
  };

  window.getJsDateFromUI = function(prefix) {
    var ds = document.getElementById(prefix + '-date').value;
    var hs = document.getElementById(prefix + '-hour').value;
    var ms = document.getElementById(prefix + '-minute').value;
    var ap = document.getElementById(prefix + '-ampm').value;
    var iso = getBackendFormat(ds, hs, ms, ap);
    if (!iso) return null;
    return new Date(iso);
  };

  window.setUIToJsDate = function(prefix, dateObj) {
    if (!dateObj || isNaN(dateObj.getTime())) return;
    document.getElementById(prefix + '-date').value = dateObj.getFullYear() + '-' + padZero(dateObj.getMonth() + 1) + '-' + padZero(dateObj.getDate());

    var h = dateObj.getHours();
    var ap = h >= 12 ? 'PM' : 'AM';
    var h12 = h % 12;
    if (h12 === 0) h12 = 12;

    document.getElementById(prefix + '-hour').value = padZero(h12);
    document.getElementById(prefix + '-minute').value = padZero(dateObj.getMinutes());
    document.getElementById(prefix + '-ampm').value = ap;
  };

  window.formatDisplayDateTime = function(dateObj) {
    if (!dateObj || isNaN(dateObj.getTime())) return '--';
    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    var d = padZero(dateObj.getDate());
    var m = months[dateObj.getMonth()];
    var y = dateObj.getFullYear();

    var h = dateObj.getHours();
    var ap = h >= 12 ? 'PM' : 'AM';
    var h12 = h % 12;
    if (h12 === 0) h12 = 12;
    var mins = padZero(dateObj.getMinutes());

    return d + ' ' + m + ' ' + y + ', ' + padZero(h12) + ':' + mins + ' ' + ap;
  };

  window.updateExpiryPreview = function(prefix) {
    var prepDate = getJsDateFromUI(prefix + '-prepared');
    var expiresInEl = document.getElementById(prefix + '-expires-in');
    var customContainer = document.getElementById(prefix + '-custom-expiry-container');
    var previewText = document.getElementById(prefix + '-expires-at-preview');
    var errorText = document.getElementById(prefix + '-expiry-error');

    var hiddenPrefix = prefix === 'add' ? 'food' : 'edit-food';
    var hiddenPrepared = document.getElementById(hiddenPrefix + '-prepared');
    var hiddenExpiry = document.getElementById(hiddenPrefix + '-expiry');

    if (!expiresInEl || !previewText || !errorText) return;

    var expiresIn = expiresInEl.value;
    errorText.style.display = 'none';

    if (!prepDate) {
      previewText.textContent = '--';
      return;
    }

    var expDate = null;
    if (expiresIn === 'custom') {
      if (customContainer) customContainer.style.display = 'flex';
      expDate = getJsDateFromUI(prefix + '-custom');
    } else {
      if (customContainer) customContainer.style.display = 'none';
      var hours = parseInt(expiresIn, 10);
      expDate = new Date(prepDate.getTime() + hours * 60 * 60 * 1000);
    }

    if (expDate && !isNaN(expDate.getTime())) {
      previewText.textContent = formatDisplayDateTime(expDate);

      var prepIso = getBackendFormat(
        document.getElementById(prefix + '-prepared-date').value,
        document.getElementById(prefix + '-prepared-hour').value,
        document.getElementById(prefix + '-prepared-minute').value,
        document.getElementById(prefix + '-prepared-ampm').value
      );
      if (hiddenPrepared) hiddenPrepared.value = prepIso;

      if (expiresIn === 'custom') {
        var expIso = getBackendFormat(
          document.getElementById(prefix + '-custom-date').value,
          document.getElementById(prefix + '-custom-hour').value,
          document.getElementById(prefix + '-custom-minute').value,
          document.getElementById(prefix + '-custom-ampm').value
        );
        if (hiddenExpiry) hiddenExpiry.value = expIso;
      } else {
        var h = expDate.getHours();
        if (hiddenExpiry) hiddenExpiry.value = expDate.getFullYear() + '-' + padZero(expDate.getMonth()+1) + '-' + padZero(expDate.getDate()) + 'T' + padZero(h) + ':' + padZero(expDate.getMinutes());
      }

      if (expDate <= prepDate) {
        errorText.style.display = 'block';
      }
    } else {
      previewText.textContent = '--';
    }
  };

  window.initDateTimeUI = function(prefix) {
    var prepDateInput = document.getElementById(prefix + '-prepared-date');
    if (!prepDateInput) return;

    populateTimeDropdowns(prefix + '-prepared');
    populateTimeDropdowns(prefix + '-custom');

    var elements = [
      prefix + '-prepared-date', prefix + '-prepared-hour', prefix + '-prepared-minute', prefix + '-prepared-ampm',
      prefix + '-expires-in',
      prefix + '-custom-date', prefix + '-custom-hour', prefix + '-custom-minute', prefix + '-custom-ampm'
    ];

    elements.forEach(function(id) {
      var el = document.getElementById(id);
      if (el) {
        el.addEventListener('change', function() { updateExpiryPreview(prefix); });
        el.addEventListener('input', function() { updateExpiryPreview(prefix); });
      }
    });
  };

  window.preventInvalidExpiry = function(e, prefix) {
    var prepDate = getJsDateFromUI(prefix + '-prepared');
    var expiresIn = document.getElementById(prefix + '-expires-in').value;
    var expDate = expiresIn === 'custom' ? getJsDateFromUI(prefix + '-custom') : new Date(prepDate.getTime() + parseInt(expiresIn, 10) * 60 * 60 * 1000);

    if (expDate <= prepDate) {
      e.preventDefault();
      var err = document.getElementById(prefix + '-expiry-error');
      if (err) err.style.display = 'block';
      return false;
    }
    return true;
  };

  document.addEventListener('DOMContentLoaded', function() {
    var path = window.location.pathname || '';

    // Always fetch user profile and notifications on dashboard load
    fetchUserProfile();
    if (typeof fetchNotifications === 'function') {
      fetchNotifications();
    }

    // Provider specific calls
    if (path.includes('/provider/')) {
      if (document.getElementById('add-prepared-date')) {
        initDateTimeUI('add');
        setUIToJsDate('add-prepared', new Date());
        updateExpiryPreview('add');
        var addForm = document.getElementById('add-food-form');
        if (addForm) {
          addForm.addEventListener('submit', function(e) { preventInvalidExpiry(e, 'add'); });
        }
      }
      if (document.getElementById('edit-prepared-date')) {
        initDateTimeUI('edit');
        var editForm = document.getElementById('edit-food-form');
        if (editForm) {
          editForm.addEventListener('submit', function(e) { preventInvalidExpiry(e, 'edit'); });
        }
      }

      if (document.getElementById('recent-listings-tbody')) {
        fetchProviderListings();
      }
      if (typeof loadProviderClaims === 'function' && document.getElementById('claims-table-body')) {
        loadProviderClaims();
      }
      if (typeof fetchProviderPickups === 'function') {
        fetchProviderPickups();
      }
    }

    // NGO specific calls
    if (path.includes('/ngo/')) {
      if (document.getElementById('ngo-recent-available-tbody')) {
        fetchNGOAvailableFood();
      }
      if (document.getElementById('ngo-claims-tbody')) {
        fetchNGOClaims();
      }
    }

    // Volunteer specific calls
    if (path.includes('/volunteer/')) {
      if (document.querySelector('#page-available .dashboard-panel__body')) {
        fetchVolunteerAvailablePickups();
        fetchVolunteerMyPickups();
      }
    }

    // Show success toast after claim redirect
    var urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('claimSuccess') === 'true') {
      // Clean URL without reloading
      window.history.replaceState({}, document.title, window.location.pathname);
      showPage('available');
      if (typeof showToast === 'function') {
        showToast('Food claimed successfully! Status: PENDING', 'success');
      }
    }
  });

})();
