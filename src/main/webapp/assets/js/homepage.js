document.addEventListener("DOMContentLoaded", function() {
    fetchPublicStats();
    fetchPublicAvailableFood();
});

function fetchPublicStats() {
    apiFetch('public/stats')
        .then(function(response) {
            if (!response.ok) throw new Error("Network response was not ok");
            return response.json();
        })
        .then(function(stats) {
            // Update Hero Stats
            updateHeroStat(0, stats.donationsRescued, "Donations Rescued");
            updateHeroStat(1, stats.foodProviders, "Food Providers");
            updateHeroStat(2, stats.ngoPartners, "NGO Partners");

            // Update Impact Stats
            updateImpactStat(0, stats.donationsRescued);
            updateImpactStat(1, stats.foodProviders);
            updateImpactStat(2, stats.ngoPartners);
            updateImpactStat(3, stats.successfulPickups);
        })
        .catch(function(error) {
            console.error("Error fetching public stats:", error);
            // Fallback gracefully without breaking the layout
        });
}

function updateHeroStat(index, value, label) {
    var statCards = document.querySelectorAll('.hero__stat-card');
    if (statCards && statCards[index]) {
        var valueElem = statCards[index].querySelector('.hero__stat-value');
        if (valueElem) {
            valueElem.textContent = value.toLocaleString() + '+';
            valueElem.setAttribute('data-count', value);
        }
    }
}

function updateImpactStat(index, value) {
    var impactItems = document.querySelectorAll('.impact-stats__item');
    if (impactItems && impactItems[index]) {
        var valueElem = impactItems[index].querySelector('.impact-stats__value');
        if (valueElem) {
            valueElem.textContent = value.toLocaleString() + '+';
            valueElem.setAttribute('data-target', value);
        }
    }
}

function fetchPublicAvailableFood() {
    var container = document.querySelector('.available-food__grid');
    if (!container) return;

    // Show loading state
    container.innerHTML = '<div style="padding:2rem;text-align:center;grid-column:1/-1;">Loading available food...</div>';

    apiFetch('public/available-food')
        .then(function(response) {
            if (!response.ok) throw new Error("Network response was not ok");
            return response.json();
        })
        .then(function(foods) {
            if (!foods || foods.length === 0) {
                container.innerHTML = '<div style="padding:2rem;text-align:center;grid-column:1/-1;">No food is currently available.<br>Please check back soon for new food rescue listings.</div>';
                return;
            }

            var html = '';
            foods.forEach(function(food) {
                // Determine image based on foodType or foodName (for premium feel)
                var imgSrc = "assets/images/food/meals.jpg";
                var nameLower = food.foodName.toLowerCase();
                if (nameLower.includes("bread") || nameLower.includes("bakery")) imgSrc = "assets/images/food/bread.jpg";
                else if (nameLower.includes("biryani") || nameLower.includes("rice")) imgSrc = "assets/images/food/biryani.jpg";

                html += '<div class="food-card reveal">';
                html += '  <img src="' + imgSrc + '" alt="' + food.foodName + '" class="food-card__img" loading="lazy">';
                html += '  <div class="food-card__body">';
                html += '    <div class="food-card__header">';
                html += '      <h5 class="food-card__name">' + food.foodName + '</h5>';
                html += '      <span class="badge badge-success">Available</span>';
                html += '    </div>';
                html += '    <div class="food-card__meta">';
                html += '      <div class="food-card__meta-item"><span class="food-card__meta-icon">🏪</span><span>' + food.providerName + '</span></div>';
                html += '      <div class="food-card__meta-item"><span class="food-card__meta-icon">📦</span><span>' + food.quantity + ' ' + food.unit + '</span></div>';
                html += '      <div class="food-card__meta-item"><span class="food-card__meta-icon">📍</span><span>' + food.pickupAddress + '</span></div>';
                html += '    </div>';
                html += '    <div class="food-card__footer">';
                html += '      <span class="food-card__expiry">⏰ Expires: ' + (food.expiryTime || 'N/A') + '</span>';
                html += '      <a href="login.html" class="btn btn-primary btn-sm">Login to Claim</a>';
                html += '    </div>';
                html += '  </div>';
                html += '</div>';
            });
            container.innerHTML = html;
        })
        .catch(function(error) {
            console.error("Error fetching available food:", error);
            container.innerHTML = '<div style="padding:2rem;text-align:center;grid-column:1/-1;color:red;">Unable to load live data. Please try again.</div>';
        });
}
