// Easy-Q Application JavaScript

// Global variables
let stompClient = null;
let isConnected = false;

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    console.log('Easy-Q Application Initialized');
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            if (alert.classList.contains('alert-success') || alert.classList.contains('alert-info')) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        });
    }, 5000);
});

// WebSocket connection functions
function connectWebSocket() {
    if (isConnected) return;
    
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function (frame) {
        console.log('WebSocket Connected: ' + frame);
        isConnected = true;
        
        // Subscribe to queue updates
        stompClient.subscribe('/topic/queue', function (message) {
            const update = JSON.parse(message.body);
            handleQueueUpdate(update);
        });
        
        // Subscribe to notification updates
        stompClient.subscribe('/topic/notifications', function (message) {
            const notification = JSON.parse(message.body);
            showNotification(notification);
        });
        
    }, function(error) {
        console.log('WebSocket Connection Error: ' + error);
        isConnected = false;
        
        // Retry connection after 5 seconds
        setTimeout(connectWebSocket, 5000);
    });
}

function disconnectWebSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
        isConnected = false;
        console.log('WebSocket Disconnected');
    }
}

// Queue update handler
function handleQueueUpdate(update) {
    console.log('Queue Update:', update);
    
    // Show toast notification
    showToast(update.message, 'info');
    
    // Update queue display if on queue page
    if (window.location.pathname.includes('/queue')) {
        updateQueueDisplay();
    }
    
    // Update admin dashboard if on admin page
    if (window.location.pathname.includes('/admin')) {
        updateAdminDashboard();
    }
}

// Notification handler
function showNotification(notification) {
    console.log('Notification:', notification);
    
    // Show toast notification
    showToast(notification.title + ': ' + notification.message, 'success');
    
    // Play notification sound (if enabled)
    playNotificationSound();
}

// Toast notification system
function showToast(message, type = 'info') {
    const toastContainer = getOrCreateToastContainer();
    
    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div class="toast" id="${toastId}" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header bg-${type} text-white">
                <i class="fas fa-${getToastIcon(type)} me-2"></i>
                <strong class="me-auto">Easy-Q</strong>
                <small class="text-white">now</small>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 5000
    });
    
    toast.show();
    
    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}

function getOrCreateToastContainer() {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
    }
    return container;
}

function getToastIcon(type) {
    const icons = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

// Sound notification
function playNotificationSound() {
    // Create audio context for notification sound
    try {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();
        
        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);
        
        oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
        oscillator.frequency.setValueAtTime(600, audioContext.currentTime + 0.1);
        
        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);
        
        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.3);
    } catch (error) {
        console.log('Audio not supported or blocked');
    }
}

// Queue display update
function updateQueueDisplay() {
    fetch('/queue/api/entries')
        .then(response => response.json())
        .then(entries => {
            // Update queue list
            const queueList = document.getElementById('queueList');
            if (queueList) {
                queueList.innerHTML = entries.map(entry => `
                    <div class="queue-item ${entry.status.toLowerCase()}" data-aos="fade-up">
                        <div class="queue-number">${entry.queueNumber}</div>
                        <div class="queue-details">
                            <div class="queue-service">${entry.serviceName}</div>
                            <div class="queue-status">${entry.status}</div>
                        </div>
                    </div>
                `).join('');
            }
            
            // Update statistics
            updateQueueStatistics(entries);
        })
        .catch(error => console.error('Error updating queue display:', error));
}

// Queue statistics update
function updateQueueStatistics(entries) {
    const stats = {
        waiting: entries.filter(e => e.status === 'WAITING').length,
        called: entries.filter(e => e.status === 'CALLED').length,
        inProgress: entries.filter(e => e.status === 'IN_PROGRESS').length,
        completed: entries.filter(e => e.status === 'COMPLETED').length
    };
    
    // Update stat elements
    Object.keys(stats).forEach(stat => {
        const element = document.getElementById(`total${stat.charAt(0).toUpperCase() + stat.slice(1)}`);
        if (element) {
            element.textContent = stats[stat];
        }
    });
}

// Admin dashboard update
function updateAdminDashboard() {
    fetch('/admin/api/stats')
        .then(response => response.json())
        .then(data => {
            // Update dashboard stats
            const statsElements = {
                'totalUsers': data.totalUsers,
                'totalAppointments': data.totalAppointments,
                'totalQueueEntries': data.totalQueueEntries,
                'activeServices': data.activeServices,
                'todayAppointments': data.todayAppointments,
                'waitingInQueue': data.waitingInQueue,
                'completedToday': data.completedToday,
                'cancelledToday': data.cancelledToday
            };
            
            Object.keys(statsElements).forEach(key => {
                const element = document.querySelector(`[data-stat="${key}"]`);
                if (element) {
                    animateNumber(element, statsElements[key]);
                }
            });
        })
        .catch(error => console.error('Error updating admin dashboard:', error));
}

// Number animation
function animateNumber(element, targetValue) {
    const currentValue = parseInt(element.textContent) || 0;
    const increment = (targetValue - currentValue) / 20;
    let current = currentValue;
    
    const timer = setInterval(() => {
        current += increment;
        if ((increment > 0 && current >= targetValue) || (increment < 0 && current <= targetValue)) {
            current = targetValue;
            clearInterval(timer);
        }
        element.textContent = Math.round(current);
    }, 50);
}

// Form validation helpers
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

// Date/time helpers
function formatDate(date) {
    return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatTime(time) {
    return new Date('2000-01-01T' + time).toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });
}

// API helpers
function apiCall(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        }
    };
    
    return fetch(url, { ...defaultOptions, ...options })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        });
}

// Error handling
function handleError(error, context = '') {
    console.error(`Error ${context}:`, error);
    showToast(`Error ${context}: ${error.message}`, 'error');
}

// Initialize WebSocket connection when page loads
if (typeof SockJS !== 'undefined' && typeof Stomp !== 'undefined') {
    connectWebSocket();
}

// Cleanup on page unload
window.addEventListener('beforeunload', function() {
    disconnectWebSocket();
});

// Export functions for global use
window.EasyQ = {
    connectWebSocket,
    disconnectWebSocket,
    showToast,
    updateQueueDisplay,
    updateAdminDashboard,
    validateForm,
    formatDate,
    formatTime,
    apiCall,
    handleError
};
