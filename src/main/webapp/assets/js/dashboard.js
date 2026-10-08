/**
 * FoodRescue — Dashboard JavaScript
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

    fetch('../provider/claim-action', {
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

    fetch('../provider/listings')
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
              recentRows += '      <div style="font-size:1.5rem;margin-right:1rem;">🍱</div>';
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
              allRows += '      <div style="font-size:1.5rem;margin-right:1rem;">🍱</div>';
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

    if (listing.preparedAt) {
      document.getElementById('edit-food-prepared').value = listing.preparedAt.replace(' ', 'T');
    } else {
      document.getElementById('edit-food-prepared').value = '';
    }
    if (listing.expiryTime) {
      document.getElementById('edit-food-expiry').value = listing.expiryTime.replace(' ', 'T');
    } else {
      document.getElementById('edit-food-expiry').value = '';
    }

    document.getElementById('edit-food-pickup').value = listing.pickupAddress || '';
    document.getElementById('edit-food-description').value = listing.description || '';

    showPage('edit-food');
  };

  // ===== Fetch NGO Available Food =====
  function fetchNGOAvailableFood() {
    var recentAvailableTbody = document.getElementById('ngo-recent-available-tbody');
    var allAvailableTbody = document.getElementById('ngo-all-available-tbody');

    if (!recentAvailableTbody && !allAvailableTbody) return;

    fetch('../ngo/available-food')
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
              html += '      <div style="font-size:1.5rem;margin-right:1rem;">🍛</div>';
              html += '      <div>';
              html += '        <div class="data-table__food-name">' + listing.foodName + '</div>';
              html += '        <div class="data-table__food-provider">Added ' + (listing.createdAt || 'recently') + '</div>';
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

    var form = document.createElement('form');
    form.method = 'POST';
    form.action = '../ngo/claim-food';
    form.style.display = 'none';

    var foodIdInput = document.createElement('input');
    foodIdInput.type = 'hidden';
    foodIdInput.name = 'foodId';
    foodIdInput.value = foodId;

    var qtyInput = document.createElement('input');
    qtyInput.type = 'hidden';
    qtyInput.name = 'claimedQuantity';
    qtyInput.value = qNum;

    form.appendChild(foodIdInput);
    form.appendChild(qtyInput);
    document.body.appendChild(form);
    form.submit();
  };
  // ===== Fetch NGO Claims =====
  function fetchNGOClaims() {
    var claimsTbody = document.getElementById('ngo-claims-tbody');
    if (!claimsTbody) return;

    fetch('../ngo/claims')
      .then(function(response) {
        if (!response.ok) throw new Error('Network error');
        return response.json();
      })
      .then(function(data) {
        var html = '';
        if (data.length === 0) {
          html = '<tr><td colspan="5" style="text-align:center;padding:2rem;">No claims found.</td></tr>';
        } else {
          data.forEach(function(claim) {
            html += '<tr>';
            html += '  <td><div class="data-table__food-name">' + claim.foodName + '</div></td>';
            html += '  <td>' + (claim.providerName || '-') + '</td>';
            html += '  <td>' + claim.claimedQuantity + ' ' + (claim.unit || '') + '</td>';

            var status = (claim.status || '').toUpperCase();
            var badgeClass = 'badge-info';
            if (status === 'PENDING' || status === 'AWAITING PICKUP') badgeClass = 'badge-warning';
            else if (status === 'APPROVED' || status === 'READY_FOR_PICKUP') badgeClass = 'badge-success';
            else if (status === 'DELIVERED') badgeClass = 'badge-success';
            else if (status === 'REJECTED') badgeClass = 'badge-danger';

            html += '  <td><span class="badge ' + badgeClass + '">' + status + '</span></td>';
            html += '  <td><button class="btn btn-sm btn-secondary">Details</button></td>';
            html += '</tr>';
          });
        }
        claimsTbody.innerHTML = html;
      })
      .catch(function(error) {
        console.error('Error fetching NGO claims:', error);
        claimsTbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:red;">Error loading claims.</td></tr>';
      });
  }

  // ===== Volunteer Dashboard Integration =====
  window.fetchVolunteerAvailablePickups = function() {
    var container = document.querySelector('#page-available .dashboard-panel__body');
    if (!container) return; // Not on volunteer dashboard

    fetch('../volunteer/available-pickups')
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
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">🏪</span><span>' + item.providerName + '</span></div>';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">🏢</span><span>' + item.ngoName + '</span></div>';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">📍</span><span>' + item.pickupAddress + '</span></div>';
            html += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">⏰</span><span>Expires: ' + item.expiryTime + '</span></div>';
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
    if (!tbody || !activeContainer) return;

    fetch('../volunteer/my-pickups')
      .then(function(res) {
        if (!res.ok) throw new Error('Network error');
        return res.json();
      })
      .then(function(data) {
        var tableHtml = '';
        var activeHtml = '';
        var activeCount = 0;

        if (!data || data.length === 0) {
          tableHtml = '<tr><td colspan="5" style="text-align:center;padding:2rem;">You have no active pickups.</td></tr>';
          activeHtml = '<div style="padding:2rem;text-align:center;">No active pickups.</div>';
        } else {
          data.forEach(function(pickup) {
            // Table view
            var statusClass = 'badge-info';
            if (pickup.status === 'ACCEPTED') statusClass = 'badge-warning';
            if (pickup.status === 'DELIVERED') statusClass = 'badge-success';

            tableHtml += '<tr>';
            tableHtml += '  <td><div class="data-table__food-name">' + pickup.foodName + '</div></td>';
            tableHtml += '  <td>' + pickup.providerName + '</td>';
            tableHtml += '  <td>' + pickup.ngoName + '</td>';
            tableHtml += '  <td><span class="badge ' + statusClass + '">' + pickup.status + '</span></td>';
            tableHtml += '  <td>';
            if (pickup.status === 'ACCEPTED') {
              tableHtml += '    <button class="btn btn-sm btn-primary" onclick="handlePickupAction(' + pickup.pickupId + ', \'PICKED_UP\', this)">Mark Picked Up</button>';
            } else if (pickup.status === 'PICKED_UP') {
              tableHtml += '    <button class="btn btn-sm btn-primary" onclick="handlePickupAction(' + pickup.pickupId + ', \'DELIVERED\', this)">Mark Delivered</button>';
            }
            tableHtml += '  </td>';
            tableHtml += '</tr>';

            // Active Card View (Only show if not delivered/cancelled)
            if (pickup.status === 'ACCEPTED' || pickup.status === 'PICKED_UP') {
              activeCount++;
              var isPickedUp = pickup.status === 'PICKED_UP';
              activeHtml += '<div class="pickup-card">';
              activeHtml += '  <div class="pickup-card__header">';
              activeHtml += '    <h4 class="pickup-card__title">' + pickup.foodName + ' &mdash; ' + pickup.quantity + ' ' + pickup.unit + '</h4>';
              activeHtml += '    <span class="badge ' + statusClass + '">' + pickup.status + '</span>';
              activeHtml += '  </div>';

              activeHtml += '  <div class="status-flow">';
              activeHtml += '    <div class="status-flow__step status-flow__step--completed">✓ Accepted</div>';
              activeHtml += '    <div class="status-flow__arrow">→</div>';
              activeHtml += '    <div class="status-flow__step ' + (isPickedUp ? 'status-flow__step--completed">✓' : 'status-flow__step--active">') + ' Picked Up</div>';
              activeHtml += '    <div class="status-flow__arrow">→</div>';
              activeHtml += '    <div class="status-flow__step">Delivered</div>';
              activeHtml += '  </div>';

              activeHtml += '  <div class="pickup-card__meta">';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">🏪</span><span>' + pickup.providerName + '</span></div>';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">🏢</span><span>' + pickup.ngoName + '</span></div>';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">📍</span><span>' + pickup.providerAddress + ' &rarr; ' + pickup.ngoAddress + '</span></div>';
              activeHtml += '    <div class="pickup-card__meta-item"><span class="pickup-card__meta-icon">⏰</span><span>Expires: ' + pickup.expiryTime + '</span></div>';
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
            activeHtml = '<div style="padding:2rem;text-align:center;">No active pickups.</div>';
          }
        }

        tbody.innerHTML = tableHtml;
        activeContainer.innerHTML = activeHtml;
      })
      .catch(function(err) {
        console.error('Failed to fetch my pickups', err);
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

    fetch('../volunteer/pickup-action', {
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

  document.addEventListener('DOMContentLoaded', function() {
    var path = window.location.pathname || '';

    // Provider specific calls
    if (path.includes('/provider/')) {
      if (document.getElementById('recent-listings-tbody')) {
        fetchProviderListings();
      }
      if (typeof loadProviderClaims === 'function' && document.getElementById('claims-table-body')) {
        loadProviderClaims();
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
