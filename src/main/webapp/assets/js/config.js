/**
 * FoodRescue Ã¢â‚¬â€ Frontend Configuration
 * Contains global settings for the application, including the API backend URL.
 */

const AppConfig = {
    // Set this to the deployed Java backend URL (e.g., 'https://api.foodrescue.com')
    // Keep it empty ('') for relative paths when hosted together on Tomcat.
    BACKEND_URL: ''
};

/**
 * Resolves a given path to the full API URL based on BACKEND_URL configuration.
 * @param {string} path - The relative path or URL
 * @returns {string} The resolved full URL
 */
function getApiUrl(path) {
    if (!AppConfig.BACKEND_URL) {
        return path;
    }

    let baseUrl = AppConfig.BACKEND_URL;
    if (!baseUrl.endsWith('/')) {
        baseUrl += '/';
    }

    // Clean up relative path markers to append to the base URL
    let cleanPath = path;
    if (cleanPath.startsWith('../')) {
        cleanPath = cleanPath.substring(3);
    } else if (cleanPath.startsWith('./')) {
        cleanPath = cleanPath.substring(2);
    } else if (cleanPath.startsWith('/')) {
        cleanPath = cleanPath.substring(1);
    }

    // If path is a full URL (e.g., loginForm.action returning absolute), don't prepend
    if (path.startsWith('http://') || path.startsWith('https://')) {
        // We still might want to replace the origin if it's the current origin
        const currentOrigin = window.location.origin;
        if (path.startsWith(currentOrigin)) {
            cleanPath = path.substring(currentOrigin.length + 1); // +1 for the slash
        } else {
            return path; // It's an external URL
        }
    }

    return baseUrl + cleanPath;
}

/**
 * Wrapper for the native fetch API.
 * Ensures that all API requests use the configured BACKEND_URL and include credentials
 * for cross-origin session/cookie support.
 * @param {string} url - The endpoint URL
 * @param {object} options - Fetch options
 * @returns {Promise<Response>}
 */
function apiFetch(url, options = {}) {
    const finalUrl = getApiUrl(url);

    const fetchOptions = {
        ...options,
        // Include credentials to send session cookies for cross-origin requests
        credentials: 'include',
    };

    // Ensure headers object exists
    if (!fetchOptions.headers) {
        fetchOptions.headers = {};
    }

    fetchOptions.headers['Accept'] = 'application/json';

    // Example CSRF protection if implemented in the future
    // const csrfToken = getCookie('CSRF-TOKEN');
    // if (csrfToken) {
    //     fetchOptions.headers['X-CSRF-TOKEN'] = csrfToken;
    // }

    return fetch(finalUrl, fetchOptions);
}
