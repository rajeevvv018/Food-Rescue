document.addEventListener('DOMContentLoaded', function() {
    var themeBtn = document.getElementById('theme-btn');
    var themePanel = document.getElementById('theme-panel');
    var themeOptions = document.querySelectorAll('.theme-option');

    // Toggle dropdown
    if (themeBtn && themePanel) {
        themeBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            themePanel.classList.toggle('active');
        });

        // Close when clicking outside
        document.addEventListener('click', function(e) {
            if (!themePanel.contains(e.target) && !themeBtn.contains(e.target)) {
                themePanel.classList.remove('active');
            }
        });
    }

    // Set theme logic
    themeOptions.forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            var theme = this.getAttribute('data-set-theme');

            if (theme === 'light') {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('foodrescue-theme', 'light');
            } else {
                document.documentElement.setAttribute('data-theme', theme);
                localStorage.setItem('foodrescue-theme', theme);
            }

            if (themePanel) {
                themePanel.classList.remove('active');
            }
        });
    });
});
