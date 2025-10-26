// Notification system for EasyQ
class NotificationManager {
    constructor() {
        this.notifications = [];
        this.setupEventListeners();
        this.startPolling();
    }

    setupEventListeners() {
        // Listen for WebSocket notifications
        if (typeof SockJS !== 'undefined' && typeof Stomp !== 'undefined') {
            this.setupWebSocket();
        }
        
        // Listen for server-sent events
        this.setupServerSentEvents();
    }

    setupWebSocket() {
        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        
        stompClient.connect({}, (frame) => {
            console.log('Connected to WebSocket');
            stompClient.subscribe('/topic/notifications', (message) => {
                const notification = JSON.parse(message.body);
                this.showNotification(notification);
            });
        });
    }

    setupServerSentEvents() {
        // Fallback to polling if WebSocket not available
        this.startPolling();
    }

    startPolling() {
        // Poll for new notifications every 10 seconds
        setInterval(() => {
            this.fetchNotifications();
        }, 10000);
    }

    async fetchNotifications() {
        try {
            const response = await fetch('/notifications/api/unsent');
            const notifications = await response.json();
            
            notifications.forEach(notification => {
                if (!this.notifications.find(n => n.id === notification.id)) {
                    this.showNotification(notification);
                    this.notifications.push(notification);
                }
            });
        } catch (error) {
            console.error('Error fetching notifications:', error);
        }
    }

    showNotification(notification) {
        // Create popup notification
        this.createPopup(notification);
        
        // Create banner notification
        this.createBanner(notification);
        
        // Show browser notification if permission granted
        this.showBrowserNotification(notification);
    }

    createPopup(notification) {
        const popup = document.createElement('div');
        popup.className = 'notification-popup';
        popup.innerHTML = `
            <div class="notification-content">
                <div class="notification-header">
                    <h4>${notification.title}</h4>
                    <button class="close-btn" onclick="this.parentElement.parentElement.parentElement.remove()">&times;</button>
                </div>
                <div class="notification-body">
                    <p>${notification.message}</p>
                    <small>${new Date(notification.createdAt).toLocaleString()}</small>
                </div>
            </div>
        `;

        // Add styles
        popup.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 9999;
            max-width: 350px;
            animation: slideIn 0.3s ease-out;
        `;

        document.body.appendChild(popup);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            if (popup.parentElement) {
                popup.remove();
            }
        }, 5000);
    }

    createBanner(notification) {
        const banner = document.createElement('div');
        banner.className = 'notification-banner';
        banner.innerHTML = `
            <div class="banner-content">
                <span class="banner-icon">🔔</span>
                <span class="banner-text">${notification.title}: ${notification.message}</span>
                <button class="banner-close" onclick="this.parentElement.parentElement.remove()">&times;</button>
            </div>
        `;

        // Add styles
        banner.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            background: #007bff;
            color: white;
            padding: 10px;
            z-index: 9998;
            text-align: center;
            animation: slideDown 0.3s ease-out;
        `;

        document.body.appendChild(banner);

        // Auto-remove after 3 seconds
        setTimeout(() => {
            if (banner.parentElement) {
                banner.remove();
            }
        }, 3000);
    }

    showBrowserNotification(notification) {
        if (Notification.permission === 'granted') {
            new Notification(notification.title, {
                body: notification.message,
                icon: '/favicon.ico'
            });
        } else if (Notification.permission !== 'denied') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    new Notification(notification.title, {
                        body: notification.message,
                        icon: '/favicon.ico'
                    });
                }
            });
        }
    }
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideDown {
        from { transform: translateY(-100%); }
        to { transform: translateY(0); }
    }
    
    .notification-popup {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }
    
    .notification-content {
        padding: 15px;
    }
    
    .notification-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
    }
    
    .notification-header h4 {
        margin: 0;
        font-size: 16px;
        color: #333;
    }
    
    .close-btn {
        background: none;
        border: none;
        font-size: 20px;
        cursor: pointer;
        color: #666;
    }
    
    .notification-body p {
        margin: 0 0 5px 0;
        color: #555;
        line-height: 1.4;
    }
    
    .notification-body small {
        color: #888;
        font-size: 12px;
    }
    
    .banner-content {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }
    
    .banner-icon {
        font-size: 18px;
    }
    
    .banner-text {
        flex: 1;
    }
    
    .banner-close {
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        cursor: pointer;
    }
`;
document.head.appendChild(style);

// Initialize notification manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.notificationManager = new NotificationManager();
});

