// --- Configuration ---
let CONFIG = {
    gatewayUrl: localStorage.getItem('gatewayUrl') || 'http://localhost:9000',
    token: localStorage.getItem('bearerToken') || ''
};

const SERVICES = {
    spaces: {
        title: 'Library Spaces',
        endpoint: '/api/spaces',
        fields: [
            { id: 'name', label: 'Space Name', type: 'text', table: true },
            { id: 'type', label: 'Type', type: 'select', table: true, options: ['STUDY_ROOM', 'MEETING_ROOM', 'COMPUTER_LAB', 'OPEN_SPACE'] },
            { id: 'capacity', label: 'Capacity', type: 'number', table: true },
            { id: 'location', label: 'Location', type: 'text', table: true }
        ]
    },
    reservations: {
        title: 'Space Reservations',
        endpoint: '/api/reservations',
        fields: [
            { id: 'spaceId', label: 'Space ID', type: 'number', table: true },
            { id: 'userId', label: 'User ID', type: 'text', table: true },
            { id: 'startTime', label: 'Start Time', type: 'datetime-local', table: true },
            { id: 'endTime', label: 'End Time', type: 'datetime-local', table: true },
            { id: 'status', label: 'Status', type: 'select', table: true, options: ['PENDING', 'CONFIRMED', 'CANCELLED'] }
        ]
    },
    emprunts: {
        title: 'Document Emprunts',
        endpoint: '/api/emprunts',
        fields: [
            { id: 'userId', label: 'User ID', type: 'number', table: true },
            { id: 'documentId', label: 'Doc ID', type: 'number', table: true },
            { id: 'documentType', label: 'Type', type: 'text', table: true },
            { id: 'empruntDate', label: 'Emprunt Date', type: 'datetime-local', table: true },
            { id: 'dueDate', label: 'Due Date', type: 'datetime-local', table: true },
            { id: 'statut', label: 'Status', type: 'text', table: true }
        ]
    },
    forum: {
        title: 'Forum Topics',
        endpoint: '/api/forum/topics',
        fields: [
            { id: 'title', label: 'Title', type: 'text', table: true },
            { id: 'description', label: 'Description', type: 'textarea', table: true },
            { id: 'authorId', label: 'Author ID', type: 'number', table: true }
        ]
    },
    fournisseurs: {
        title: 'Suppliers (Fournisseurs)',
        endpoint: '/api/fournisseurs',
        fields: [
            { id: 'nom', label: 'Name', type: 'text', table: true },
            { id: 'email', label: 'Email', type: 'email', table: true },
            { id: 'telephone', label: 'Phone', type: 'text', table: true },
            { id: 'adresse', label: 'Address', type: 'text', table: true }
        ]
    },
    inventaire: {
        title: 'Inventory (Books)',
        endpoint: '/api/inventaire/books',
        fields: [
            { id: 'title', label: 'Title', type: 'text', table: true },
            { id: 'author', label: 'Author', type: 'text', table: true },
            { id: 'isbn', label: 'ISBN', type: 'text', table: true },
            { id: 'category', label: 'Category', type: 'text', table: true }
        ]
    },
    reclamations: {
        title: 'Complaints (Reclamations)',
        endpoint: '/api/reclamations',
        fields: [
            { id: 'userId', label: 'User ID', type: 'text', table: true },
            { id: 'sujet', label: 'Subject', type: 'text', table: true },
            { id: 'description', label: 'Description', type: 'textarea', table: true },
            { id: 'statut', label: 'Status', type: 'select', table: true, options: ['OUVERTE', 'EN_COURS', 'RESOLUE'] }
        ]
    }
};

let currentView = 'dashboard';
let editingId = null;

// --- DOM Elements ---
const navItems = document.querySelectorAll('.sidebar-nav li');
const dashboardView = document.getElementById('view-dashboard');
const contentView = document.getElementById('view-content');
const viewTitle = document.getElementById('view-title');
const tableHeader = document.getElementById('table-header');
const tableBody = document.getElementById('table-body');
const loadingOverlay = document.getElementById('loading');
const modalOverlay = document.getElementById('modal-overlay');
const dynamicForm = document.getElementById('dynamic-form');
const addBtn = document.getElementById('add-btn');

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
    setupNavigation();
    setupModals();
    setupAuth();
    updateDashboardStats();
});

function setupNavigation() {
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const view = item.getAttribute('data-view');
            switchView(view);
            
            navItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
        });
    });
}

function switchView(view) {
    currentView = view;
    if (view === 'dashboard') {
        dashboardView.classList.remove('hidden');
        contentView.classList.add('hidden');
        addBtn.classList.add('hidden');
        updateDashboardStats();
    } else {
        dashboardView.classList.add('hidden');
        contentView.classList.remove('hidden');
        addBtn.classList.remove('hidden');
        document.getElementById('add-btn-text').innerText = `Add ${view.slice(0, -1)}`;
        renderView(view);
    }
}

async function renderView(serviceKey) {
    const service = SERVICES[serviceKey];
    viewTitle.innerText = service.title;
    
    // Render Header
    tableHeader.innerHTML = '';
    service.fields.filter(f => f.table).forEach(f => {
        const th = document.createElement('th');
        th.innerText = f.label;
        tableHeader.appendChild(th);
    });
    const actionTh = document.createElement('th');
    actionTh.innerText = 'Actions';
    actionTh.style.textAlign = 'right';
    tableHeader.appendChild(actionTh);

    fetchData(serviceKey);
}

async function fetchData(serviceKey) {
    showLoading(true);
    const service = SERVICES[serviceKey];
    
    try {
        const response = await apiFetch(service.endpoint);
        let data = [];
        
        if (Array.isArray(response)) {
            data = response;
        } else if (response && response.data && Array.isArray(response.data)) {
            data = response.data;
        } else if (response && response.data && response.data.content) {
            data = response.data.content;
        }

        renderTable(serviceKey, data);
    } catch (error) {
        console.error('Fetch error:', error);
        let msg = `Error connecting to ${service.title}`;
        if (error.message.includes('401')) msg += ' (Unauthorized - Please provide a Bearer Token in Auth Settings)';
        else if (error.message.includes('403')) msg += ' (Forbidden - Origin blocked or Token invalid)';
        else if (error.message.includes('Failed to fetch')) msg += ' (CORS or Gateway Offline - Try starting a local server or restart Gateway)';
        else msg += ` (${error.message})`;

        tableBody.innerHTML = `<tr><td colspan="100%" style="text-align:center; color:#ff5252; padding: 40px;">
            <i class="fas fa-exclamation-triangle" style="font-size: 2rem; margin-bottom: 10px; display: block;"></i>
            ${msg}<br>
            <small style="color: #b0b0b5; margin-top: 10px; display: block;">Target: ${CONFIG.gatewayUrl}${service.endpoint}</small>
        </td></tr>`;
    } finally {
        showLoading(false);
    }
}

function renderTable(serviceKey, data) {
    const service = SERVICES[serviceKey];
    tableBody.innerHTML = '';

    if (!data || data.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="100%" style="text-align:center">No records found</td></tr>`;
        return;
    }

    data.forEach(item => {
        const tr = document.createElement('tr');
        
        service.fields.filter(f => f.table).forEach(f => {
            const td = document.createElement('td');
            let val = item[f.id];
            if (f.type === 'datetime-local' && val) val = new Date(val).toLocaleString();
            td.innerText = val !== undefined ? val : '-';
            tr.appendChild(td);
        });

        const actionsTd = document.createElement('td');
        actionsTd.className = 'actions-cell';
        actionsTd.style.justifyContent = 'flex-end';
        
        const idField = serviceKey === 'inventaire' ? 'bookId' : 'id';
        const itemId = item[idField];

        actionsTd.innerHTML = `
            <button class="action-btn edit-btn" onclick="openEditModal('${serviceKey}', ${JSON.stringify(item).replace(/"/g, '&quot;')})">
                <i class="fas fa-edit"></i>
            </button>
            <button class="action-btn delete-btn" onclick="deleteItem('${serviceKey}', '${itemId}')">
                <i class="fas fa-trash"></i>
            </button>
        `;
        tr.appendChild(actionsTd);
        tableBody.appendChild(tr);
    });
}

// --- API Helpers ---
async function apiFetch(path, options = {}) {
    const url = `${CONFIG.gatewayUrl}${path}`;
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (CONFIG.token) {
        headers['Authorization'] = `Bearer ${CONFIG.token}`;
    }

    const response = await fetch(url, { ...options, headers });
    if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);
    
    if (response.status === 204) return null;
    return await response.json();
}

// --- CRUD Operations ---
async function deleteItem(serviceKey, id) {
    if (!confirm('Are you sure you want to delete this item?')) return;
    
    const service = SERVICES[serviceKey];
    showLoading(true);
    try {
        await apiFetch(`${service.endpoint}/${id}`, { method: 'DELETE' });
        fetchData(serviceKey);
    } catch (error) {
        alert('Delete failed: ' + error.message);
        showLoading(false);
    }
}

async function saveItem() {
    const service = SERVICES[currentView];
    const formData = new FormData(dynamicForm);
    const payload = {};
    formData.forEach((value, key) => {
        payload[key] = value;
    });

    showLoading(true);
    try {
        const method = editingId ? 'PUT' : 'POST';
        const path = editingId ? `${service.endpoint}/${editingId}` : service.endpoint;
        
        await apiFetch(path, {
            method: method,
            body: JSON.stringify(payload)
        });
        
        closeModal();
        fetchData(currentView);
    } catch (error) {
        alert('Save failed: ' + error.message);
        showLoading(false);
    }
}

// --- UI Components ---
function showLoading(show) {
    loadingOverlay.classList.toggle('hidden', !show);
}

function setupModals() {
    addBtn.addEventListener('click', () => openAddModal());
    document.getElementById('close-modal').addEventListener('click', closeModal);
    document.getElementById('cancel-modal').addEventListener('click', closeModal);
    document.getElementById('save-btn').addEventListener('click', saveItem);
}

function openAddModal() {
    editingId = null;
    document.getElementById('modal-title').innerText = `Add ${currentView.slice(0, -1)}`;
    renderForm();
    modalOverlay.classList.remove('hidden');
}

window.openEditModal = (serviceKey, item) => {
    const idField = serviceKey === 'inventaire' ? 'bookId' : 'id';
    editingId = item[idField];
    document.getElementById('modal-title').innerText = `Edit ${serviceKey.slice(0, -1)}`;
    renderForm(item);
    modalOverlay.classList.remove('hidden');
};

function renderForm(item = null) {
    const service = SERVICES[currentView];
    dynamicForm.innerHTML = '';
    
    service.fields.forEach(f => {
        const group = document.createElement('div');
        group.className = 'form-group';
        
        const label = document.createElement('label');
        label.innerText = f.label;
        group.appendChild(label);

        let input;
        if (f.type === 'select') {
            input = document.createElement('select');
            f.options.forEach(opt => {
                const o = document.createElement('option');
                o.value = opt;
                o.innerText = opt;
                input.appendChild(o);
            });
        } else if (f.type === 'textarea') {
            input = document.createElement('textarea');
            input.rows = 3;
        } else {
            input = document.createElement('input');
            input.type = f.type;
        }
        
        input.name = f.id;
        if (item && item[f.id]) {
            // Format datetime for input
            if (f.type === 'datetime-local') {
                input.value = new Date(item[f.id]).toISOString().slice(0, 16);
            } else {
                input.value = item[f.id];
            }
        }
        
        group.appendChild(input);
        dynamicForm.appendChild(group);
    });
}

function closeModal() {
    modalOverlay.classList.add('hidden');
}

function setupAuth() {
    const trigger = document.getElementById('auth-trigger');
    const modal = document.getElementById('auth-modal-overlay');
    const save = document.getElementById('save-auth');
    
    trigger.addEventListener('click', () => modal.classList.remove('hidden'));
    document.getElementById('close-auth-modal').addEventListener('click', () => modal.classList.add('hidden'));
    
    save.addEventListener('click', () => {
        CONFIG.gatewayUrl = document.getElementById('gateway-url').value;
        CONFIG.token = document.getElementById('bearer-token').value;
        localStorage.setItem('gatewayUrl', CONFIG.gatewayUrl);
        localStorage.setItem('bearerToken', CONFIG.token);
        modal.classList.add('hidden');
        if (currentView !== 'dashboard') fetchData(currentView);
    });
}

async function updateDashboardStats() {
    const services = ['spaces', 'reservations', 'emprunts'];
    services.forEach(async s => {
        try {
            const data = await apiFetch(SERVICES[s].endpoint);
            const count = Array.isArray(data) ? data.length : (data.data?.totalElements || data.data?.length || 0);
            document.getElementById(`count-${s}`).innerText = count;
        } catch (e) {
            document.getElementById(`count-${s}`).innerText = 'Err';
        }
    });
}

window.refreshCurrentView = () => fetchData(currentView);
