// ===== NOTIFICATION BELL =====
//const notifBell = document.getElementById('notifBell');
//
//notifBell.addEventListener('click', function () {
//    const badge = document.getElementById('notifBadge');
//    if (badge) {
//        badge.style.display = 'none';
//    }
//});

// ===== CONNECT BUTTON =====
function handleConnect(btn, eventId, adminId, eventName) {
    if (!adminId || adminId === -1 || adminId === 0 || adminId === '-1' || adminId === '0') {
        showToast('Cannot connect: Invalid event organizer');
        console.error('Invalid adminId:', adminId, 'for event:', eventName);
        return;
    }

    if (btn.classList.contains('connected')) {
        return;
    }

    const params = new URLSearchParams();
    params.append('targetAdminId', adminId);
    params.append('eventName', eventName);

    fetch('/new_common/ConnectServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
        .then(response => response.text())
        .then(result => {
            if (result.trim() === 'success') {
                btn.classList.add('connected');
                btn.textContent = 'Connected ✓';
                showToast('Connection request sent to event organizer!');
            } else {
                showToast('Failed to send request. Please try again.');
            }
        })
        .catch(err => {
            console.error('Connection error:', err);
            showToast('Error sending connection request');
        });
}

// ===== FILTER PANEL TOGGLE =====
const filterToggle = document.getElementById('filterToggle');
const filterPanel = document.getElementById('filterPanel');

filterToggle.addEventListener('click', function () {
    const isOpen = filterPanel.classList.toggle('open');
    filterToggle.classList.toggle('open', isOpen);
});

// ===== FILTER VARIABLES =====
let activeLocation = 'all';
let activeLevel = 'all';
let activeSports = 'all';  // ✅ NEW: Sports filter variable

// ===== FILTER CLICK =====
const chips = document.querySelectorAll('.chip');

chips.forEach(chip => {
    chip.addEventListener('click', function () {
        const filterType = this.dataset.filter;
        const value = this.dataset.value;

        document.querySelectorAll(`.chip[data-filter="${filterType}"]`)
            .forEach(c => c.classList.remove('active'));

        this.classList.add('active');

        if (filterType === 'location') activeLocation = value;
        if (filterType === 'level') activeLevel = value;
        if (filterType === 'sports') activeSports = value;  // ✅ NEW

        applyFilters();
        updateFilterCount();
    });
});

// ===== APPLY FILTERS =====
function applyFilters() {
    const cards = document.querySelectorAll('#matchList .user-card');
    let visibleCount = 0;

    cards.forEach(card => {
        const km = parseFloat(card.dataset.km);
        const level = card.dataset.level;
        const sports = card.dataset.sports;  // ✅ NEW

        const locationOk = activeLocation === 'all' || km <= parseFloat(activeLocation);
        const levelOk = activeLevel === 'all' || level === activeLevel;
        const sportsOk = activeSports === 'all' || sports === activeSports;  // ✅ NEW

        if (locationOk && levelOk && sportsOk) {
            card.style.display = '';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });

    const emptyState = document.getElementById('emptyState');
    emptyState.style.display = visibleCount === 0 ? 'flex' : 'none';
}

// ===== FILTER COUNT =====
function updateFilterCount() {
    const countEl = document.getElementById('filterCount');
    let count = 0;

    if (activeLocation !== 'all') count++;
    if (activeLevel !== 'all') count++;
    if (activeSports !== 'all') count++;  // ✅ NEW

    if (count > 0) {
        countEl.textContent = count;
        countEl.style.display = 'inline-flex';
    } else {
        countEl.style.display = 'none';
    }
}

// ===== CLEAR FILTERS =====
const clearBtn = document.getElementById('clearFilters');

clearBtn.addEventListener('click', function () {
    activeLocation = 'all';
    activeLevel = 'all';
    activeSports = 'all';  // ✅ NEW

    document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
    document.querySelector('.chip[data-filter="location"][data-value="all"]').classList.add('active');
    document.querySelector('.chip[data-filter="level"][data-value="all"]').classList.add('active');

    // ✅ NEW: Reset sports filter
    const sportsAllChip = document.querySelector('.chip[data-filter="sports"][data-value="all"]');
    if (sportsAllChip) sportsAllChip.classList.add('active');

    applyFilters();
    updateFilterCount();
});

// ===== MODAL =====
const modalOverlay = document.getElementById('modalOverlay');
const openModalBtn = document.getElementById('openModal');
const closeModalBtn = document.getElementById('closeModal');
const cancelModalBtn = document.getElementById('cancelModal');

openModalBtn.addEventListener('click', function () {
    modalOverlay.classList.add('open');
    document.getElementById('eventName').focus();
});

function closeModal() {
    modalOverlay.classList.remove('open');
}

closeModalBtn.addEventListener('click', closeModal);
cancelModalBtn.addEventListener('click', closeModal);

// ===== LEVEL MODAL =====
const levelModalOverlay = document.getElementById('levelModalOverlay');
const openLevelModalBtn = document.getElementById('openLevelModal');
const closeLevelModalBtn = document.getElementById('closeLevelModal');
const cancelLevelModalBtn = document.getElementById('cancelLevelModal');
const saveLevelBtn = document.getElementById('saveLevelBtn');
const userLevelSelect = document.getElementById('userLevelSelect');
const currentLevelDisplay = document.getElementById('currentLevelDisplay');

// Get current user level from session
let currentUserLevel = '';
fetch('/new_common/getUserLevel')
    .then(res => res.json())
    .then(data => {
        currentUserLevel = data.level || 'Beginner';
        if (currentLevelDisplay) {
            currentLevelDisplay.textContent = currentUserLevel;
        }
        if (userLevelSelect) {
            userLevelSelect.value = currentUserLevel;
        }
    })
    .catch(err => console.error('Error getting user level:', err));

openLevelModalBtn.addEventListener('click', function () {
    levelModalOverlay.classList.add('open');
});

function closeLevelModal() {
    levelModalOverlay.classList.remove('open');
}

closeLevelModalBtn.addEventListener('click', closeLevelModal);
cancelLevelModalBtn.addEventListener('click', closeLevelModal);

// Save level changes
saveLevelBtn.addEventListener('click', function () {
    const newLevel = userLevelSelect.value;

    fetch('/new_common/updateUserLevel', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'level=' + encodeURIComponent(newLevel)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Level updated to ' + newLevel + '!');
                currentUserLevel = newLevel;
                if (currentLevelDisplay) {
                    currentLevelDisplay.textContent = newLevel;
                }
                // Update the stat card
                const levelStatEl = document.querySelector('.stat-card:nth-child(3) .stat-value');
                if (levelStatEl) {
                    levelStatEl.textContent = newLevel;
                }
                closeLevelModal();
            } else {
                showToast('Failed to update level: ' + (data.message || 'Unknown error'));
            }
        })
        .catch(err => {
            console.error('Error updating level:', err);
            showToast('Error updating level');
        });
});

// ===== CLOSE ON OUTSIDE CLICK =====
modalOverlay.addEventListener('click', function (e) {
    if (e.target === modalOverlay) closeModal();
});

levelModalOverlay.addEventListener('click', function (e) {
    if (e.target === levelModalOverlay) closeLevelModal();
});

// ===== CLOSE ON ESC =====
document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && modalOverlay.classList.contains('open')) closeModal();
    if (e.key === 'Escape' && levelModalOverlay.classList.contains('open')) closeLevelModal();
});

// ===== BASIC VALIDATION =====
const form = document.getElementById('eventForm');

form.addEventListener('submit', function (e) {
    const name = document.getElementById('eventName').value.trim();
    const address = document.getElementById('eventAddress').value.trim();
    const city = document.getElementById('eventCity').value.trim();
    const pincode = document.getElementById('eventPincode').value.trim();

    if (!name) {
        e.preventDefault();
        const input = document.getElementById('eventName');
        input.focus();
        input.style.borderColor = '#e63946';
        showToast('Please enter an event name');
        return;
    }

    if (!address) {
        e.preventDefault();
        const input = document.getElementById('eventAddress');
        input.focus();
        input.style.borderColor = '#e63946';
        showToast('Please enter an address');
        return;
    }

    if (!city) {
        e.preventDefault();
        const input = document.getElementById('eventCity');
        input.focus();
        input.style.borderColor = '#e63946';
        showToast('Please enter a city');
        return;
    }

    if (!pincode || pincode.length !== 6) {
        e.preventDefault();
        const input = document.getElementById('eventPincode');
        input.focus();
        input.style.borderColor = '#e63946';
        showToast('Please enter a valid 6-digit pincode');
        return;
    }
});

// ===== RESET BORDER ON INPUT =====
document.getElementById('eventName').addEventListener('input', function () { this.style.borderColor = ''; });
document.getElementById('eventAddress').addEventListener('input', function () { this.style.borderColor = ''; });
document.getElementById('eventCity').addEventListener('input', function () { this.style.borderColor = ''; });
document.getElementById('eventPincode').addEventListener('input', function () { this.style.borderColor = ''; });

// ===== TOAST =====
function showToast(message) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(function () { toast.classList.remove('show'); }, 3000);
}

// ===== GEOLOCATION =====
function saveUserLocation(lat, lng) {
    fetch('/new_common/getData', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'latitude=' + lat + '&longitude=' + lng
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log('Location saved successfully');
            }
        })
        .catch(err => console.error('Location save error:', err));

    const latField = document.getElementById('eventLat');
    const lngField = document.getElementById('eventLng');
    if (latField && lngField) {
        latField.value = lat;
        lngField.value = lng;
    }
}

if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function (pos) {
            saveUserLocation(pos.coords.latitude, pos.coords.longitude);
        },
        function (err) {
            console.warn('Geolocation denied or unavailable. Using city fallback.');
            if (err.code === 1) {
                showToast('Please enable location to see nearby matches');
            }
        }
    );
}

// ===== LOAD STATS =====
function loadStats() {
    fetch('/new_common/getStats')
        .then(res => res.json())
        .then(data => {
            if (data.nearbyMatches !== undefined) {
                const nearbyEl = document.querySelector('.stat-card:nth-child(1) .stat-value');
                if (nearbyEl) nearbyEl.textContent = data.nearbyMatches;
            }
            if (data.upcomingGames !== undefined) {
                const upcomingEl = document.getElementById('upcomingCount');
                if (upcomingEl) upcomingEl.textContent = data.upcomingGames;
            }
        })
        .catch(err => console.error('Stats load error:', err));
}

loadStats();

// ===== LOAD EVENTS INTO MATCH LIST =====
const matchList = document.getElementById('matchList');
let uniqueSports = new Set();  // ✅ NEW: Track unique sports for filters

function loadMatches() {
    fetch('/new_common/getData')
        .then(res => res.json())
        .then(events => {
            console.log('Events loaded:', events);

            matchList.innerHTML = '';

            if (!Array.isArray(events) || events.length === 0) {
                document.getElementById('emptyState').style.display = 'flex';
                return;
            }

            document.getElementById('emptyState').style.display = 'none';

            // ✅ NEW: Collect unique sports for filters
            uniqueSports.clear();

            events.forEach(ev => {
                if (ev.sports && ev.sports.trim() !== '') {
                    uniqueSports.add(ev.sports);
                }
            });

            // ✅ NEW: Update sports filter chips
            updateSportsFilterChips();

            events.forEach(ev => {
                const randomImg = "https://i.pravatar.cc/64?img=" + Math.floor(Math.random() * 70);

                let km = 'Same city';
                if (ev.distance_km && ev.distance_km > 0) {
                    km = ev.distance_km + ' km away';
                }

                const level = (ev.level || 'any').toLowerCase();
                const sports = (ev.sports || 'other').toLowerCase();

                const card = document.createElement('div');
                card.className = 'user-card';
                card.dataset.km = ev.distance_km || 0;
                card.dataset.level = level;
                card.dataset.sports = sports;  // ✅ NEW

                card.innerHTML = `
                    <img src="${randomImg}" class="user-avatar" alt="${ev.name}">
                    <div class="user-info">
                        <div class="user-header">
                            <span class="user-name">${escapeHtml(ev.name)}</span>
                            <span class="tag">${escapeHtml(ev.sports || '')}</span>
                            <span class="tag">${escapeHtml(ev.level || '')}</span>
                        </div>
                        <div class="user-location">
                            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="#888" stroke-width="2">
                                <path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 0 1 18 0z"/>
                                <circle cx="12" cy="10" r="3"/>
                            </svg>
                            ${escapeHtml(ev.city || 'Unknown')} • ${escapeHtml(km)}
                        </div>
                        <div class="user-interests">
                            <span class="interest-tag">${escapeHtml(ev.eventDate || '')} ${escapeHtml(ev.eventTime || '')}</span>
                            <span class="interest-tag">${escapeHtml(ev.address || '')}</span>
                        </div>
                        <p class="user-bio">${escapeHtml(ev.description || 'No description provided.')}</p>
                    </div>
                    <div class="user-actions">
                        <button class="btn-connect" onclick="handleConnect(this, ${ev.eventId}, ${ev.adminId}, '${escapeHtml(ev.name)}')">Connect</button>
                        <button class="btn-message" onclick="window.location.href='/new_common/DiscussionServlet?event_id=${ev.eventId}'">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                            </svg>
                            Message
                        </button>
                    </div>
                `;
                matchList.appendChild(card);
            });

            applyFilters();
        })
        .catch(err => console.error('Match load error:', err));
}

// ✅ NEW: Function to update sports filter chips dynamically
function updateSportsFilterChips() {
    const sportsContainer = document.getElementById('sportsFilterChips');
    if (!sportsContainer) return;

    // Clear existing sports chips (keep the "All" button)
    const allChip = sportsContainer.querySelector('.chip[data-value="all"]');
    sportsContainer.innerHTML = '';
    sportsContainer.appendChild(allChip);

    // Add chips for each unique sport
    const sortedSports = Array.from(uniqueSports).sort();
    sortedSports.forEach(sport => {
        const chip = document.createElement('button');
        chip.className = 'chip';
        chip.setAttribute('data-filter', 'sports');
        chip.setAttribute('data-value', sport.toLowerCase());
        chip.textContent = sport;
        chip.addEventListener('click', function() {
            const filterType = this.dataset.filter;
            const value = this.dataset.value;

            document.querySelectorAll(`.chip[data-filter="${filterType}"]`)
                .forEach(c => c.classList.remove('active'));
            this.classList.add('active');

            if (filterType === 'sports') activeSports = value;
            applyFilters();
            updateFilterCount();
        });
        sportsContainer.appendChild(chip);
    });
}

// Helper function to prevent XSS attacks
function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Initial load
loadMatches();
loadStats();

// Auto-refresh every 30 seconds
setInterval(loadMatches, 30000);