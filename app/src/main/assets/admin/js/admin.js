// ThunderNet Admin - Main JavaScript
let currentModule = 'dashboard';
let serverConfig = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Cargar configuración del servidor
    loadServerConfig();
    
    // Configurar navegación
    setupNavigation();
    
    // Cargar módulo inicial
    loadModule('dashboard');
    
    // Verificar conexión
    checkConnection();
    
    // Configurar eventos
    setupEvents();
});

function loadServerConfig() {
    try {
        if (typeof Android !== 'undefined' && Android.getServerConfig) {
            const config = JSON.parse(Android.getServerConfig());
            serverConfig = config;
            updateConnectionStatus(config.connected);
        } else {
            serverConfig = {
                ip: 'localhost',
                databases: {
                    auth: 'auth',
                    characters: 'characters',
                    world: 'world'
                },
                connected: false
            };
            updateConnectionStatus(false);
        }
    } catch (error) {
        console.error('Error loading config:', error);
        serverConfig = null;
    }
}

function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            const module = this.getAttribute('data-module');
            
            // Actualizar estado activo
            navItems.forEach(nav => nav.classList.remove('active'));
            this.classList.add('active');
            
            // Cargar módulo
            loadModule(module);
        });
    });
}

function loadModule(moduleName) {
    currentModule = moduleName;
    const contentArea = document.getElementById('contentArea');
    
    // Actualizar breadcrumb
    document.getElementById('currentModule').textContent = 
        getModuleTitle(moduleName);
    
    // Mostrar spinner de carga
    contentArea.innerHTML = `
        <div class="spinner"></div>
        <p style="text-align: center; color: var(--text-secondary); margin-top: 10px;">
            Cargando módulo...
        </p>
    `;
    
    // Cargar contenido del módulo
    setTimeout(() => {
        fetchModuleContent(moduleName)
            .then(html => {
                contentArea.innerHTML = html;
                initModule(moduleName);
            })
            .catch(error => {
                contentArea.innerHTML = `
                    <div style="text-align: center; padding: 50px;">
                        <i class="fas fa-exclamation-triangle" style="font-size: 3rem; color: var(--danger);"></i>
                        <h3 style="margin: 20px 0 10px; color: var(--text-primary);">Error al cargar el módulo</h3>
                        <p style="color: var(--text-secondary);">${error.message}</p>
                        <button class="btn btn-primary" onclick="loadModule('${moduleName}')">
                            <i class="fas fa-redo"></i> Reintentar
                        </button>
                    </div>
                `;
            });
    }, 300);
}

async function fetchModuleContent(moduleName) {
    // Intentar cargar desde servidor
    if (serverConfig && serverConfig.connected) {
        try {
            const response = await fetch(`/admin/modules/${moduleName}.html`);
            if (response.ok) {
                return await response.text();
            }
        } catch (error) {
            console.log('Falling back to local module:', error);
        }
    }
    
    // Cargar desde assets locales
    try {
        const response = await fetch(`file:///android_asset/admin/modules/${moduleName}.html`);
        const text = await response.text();
        return text;
    } catch (error) {
        // Si no existe el módulo, mostrar contenido por defecto
        return getDefaultModuleContent(moduleName);
    }
}

function getDefaultModuleContent(moduleName) {
    const modules = {
        dashboard: `
            <div class="dashboard">
                <div class="dashboard-header">
                    <h1 class="dashboard-title">Panel de Control</h1>
                    <div class="dashboard-actions">
                        <button class="btn btn-primary" onclick="checkConnection()">
                            <i class="fas fa-sync-alt"></i> Verificar Conexión
                        </button>
                    </div>
                </div>
                
                <div class="stats-cards">
                    <div class="stat-card">
                        <i class="fas fa-server module-icon"></i>
                        <div class="stat-value" id="serverStatus">Offline</div>
                        <div class="stat-label">Estado del Servidor</div>
                    </div>
                    <div class="stat-card">
                        <i class="fas fa-users module-icon"></i>
                        <div class="stat-value" id="totalAccounts">0</div>
                        <div class="stat-label">Cuentas Totales</div>
                    </div>
                    <div class="stat-card">
                        <i class="fas fa-user-shield module-icon"></i>
                        <div class="stat-value" id="onlinePlayers">0</div>
                        <div class="stat-label">Jugadores Online</div>
                    </div>
                    <div class="stat-card">
                        <i class="fas fa-globe module-icon"></i>
                        <div class="stat-value" id="activeRealms">0</div>
                        <div class="stat-label">Reinos Activos</div>
                    </div>
                </div>
                
                <h2 style="margin: 30px 0 20px; color: var(--accent-blue);">Módulos Disponibles</h2>
                <div class="modules-grid">
                    <div class="module-card" onclick="loadModule('servers')">
                        <i class="fas fa-server module-icon"></i>
                        <h3 class="module-title">Estado de Servidores</h3>
                        <p class="module-desc">Monitorea el estado de todos los servidores en tiempo real</p>
                    </div>
                    
                    <div class="module-card" onclick="loadModule('accounts')">
                        <i class="fas fa-users module-icon"></i>
                        <h3 class="module-title">Gestión de Cuentas</h3>
                        <p class="module-desc">Administra cuentas de usuarios, permisos y seguridad</p>
                    </div>
                    
                    <div class="module-card" onclick="loadModule('players')">
                        <i class="fas fa-user-shield module-icon"></i>
                        <h3 class="module-title">Gestión de Jugadores</h3>
                        <p class="module-desc">Administra personajes, inventarios y estadísticas</p>
                    </div>
                    
                    <div class="module-card" onclick="loadModule('realms')">
                        <i class="fas fa-globe module-icon"></i>
                        <h3 class="module-title">Gestión de Reinós</h3>
                        <p class="module-desc">Configura y monitorea reinos del servidor</p>
                    </div>
                    
                    <div class="module-card" onclick="loadModule('commands')">
                        <i class="fas fa-terminal module-icon"></i>
                        <h3 class="module-title">Comandos en Tiempo Real</h3>
                        <p class="module-desc">Ejecuta comandos de GM y administración</p>
                    </div>
                    
                    <div class="module-card" onclick="loadModule('items')">
                        <i class="fas fa-cube module-icon"></i>
                        <h3 class="module-title">Gestión de Items</h3>
                        <p class="module-desc">Administra items, drop rates y propiedades</p>
                    </div>
                </div>
            </div>
        `,
        
        accounts: `
            <div class="accounts-module">
                <div class="module-header">
                    <h2>Gestión de Cuentas</h2>
                    <div class="module-actions">
                        <button class="btn btn-primary" onclick="showCreateAccountModal()">
                            <i class="fas fa-plus"></i> Nueva Cuenta
                        </button>
                    </div>
                </div>
                
                <div class="search-bar">
                    <input type="text" id="accountSearch" placeholder="Buscar cuentas por usuario o email..." 
                           style="width: 100%; padding: 12px; background: var(--card-bg); 
                                  border: 1px solid var(--border-color); color: var(--text-primary);
                                  border-radius: 5px;">
                </div>
                
                <div class="table-container">
                    <div class="table-header">
                        <h3 class="table-title">Cuentas Registradas</h3>
                        <div class="table-actions">
                            <button class="btn btn-secondary" onclick="refreshAccounts()">
                                <i class="fas fa-sync-alt"></i>
                            </button>
                        </div>
                    </div>
                    <div class="table-content">
                        <table id="accountsTable">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Usuario</th>
                                    <th>Email</th>
                                    <th>GM Level</th>
                                    <th>Estado</th>
                                    <th>Último Login</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="accountsTableBody">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                                        <i class="fas fa-database" style="font-size: 2rem; margin-bottom: 10px; display: block;"></i>
                                        No hay conexión a la base de datos
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `,
        
        players: `
            <div class="players-module">
                <div class="module-header">
                    <h2>Gestión de Jugadores</h2>
                </div>
                
                <div class="search-container">
                    <div class="input-group">
                        <label>Buscar Personaje</label>
                        <div style="display: flex; gap: 10px;">
                            <input type="text" id="playerSearch" placeholder="Nombre del personaje..." 
                                   style="flex: 1;">
                            <button class="btn btn-primary" onclick="searchPlayer()">
                                <i class="fas fa-search"></i> Buscar
                            </button>
                        </div>
                    </div>
                </div>
                
                <div id="playerResults">
                    <div class="table-container">
                        <div class="table-header">
                            <h3 class="table-title">Personajes</h3>
                        </div>
                        <div class="table-content">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Nombre</th>
                                        <th>Raza</th>
                                        <th>Clase</th>
                                        <th>Nivel</th>
                                        <th>Reino</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="playersTableBody">
                                    <tr>
                                        <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                                            Ingresa un nombre para buscar personajes
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        `,
        
        settings: `
            <div class="settings-module">
                <div class="module-header">
                    <h2>Configuración del Servidor</h2>
                </div>
                
                <div class="settings-form">
                    <div class="input-group">
                        <label>Dirección IP del Servidor</label>
                        <input type="text" id="serverIp" value="${serverConfig?.ip || 'localhost'}">
                    </div>
                    
                    <div class="input-group">
                        <label>Puerto MySQL</label>
                        <input type="text" id="serverPort" value="3306">
                    </div>
                    
                    <div class="database-section">
                        <h3 style="margin: 30px 0 20px; color: var(--accent-blue);">Bases de Datos</h3>
                        
                        <div class="input-group">
                            <label>Base de Datos Auth</label>
                            <input type="text" id="authDb" value="${serverConfig?.databases?.auth || 'auth'}">
                        </div>
                        
                        <div class="input-group">
                            <label>Base de Datos Characters</label>
                            <input type="text" id="charsDb" value="${serverConfig?.databases?.characters || 'characters'}">
                        </div>
                        
                        <div class="input-group">
                            <label>Base de Datos World</label>
                            <input type="text" id="worldDb" value="${serverConfig?.databases?.world || 'world'}">
                        </div>
                    </div>
                    
                    <div class="services-section">
                        <h3 style="margin: 30px 0 20px; color: var(--accent-blue);">Servicios</h3>
                        
                        <div class="input-group">
                            <label>Puerto SOAP</label>
                            <input type="text" id="soapPort" value="${serverConfig?.soap_port || '7878'}">
                        </div>
                        
                        <div class="input-group">
                            <label>Puerto RA (Remote Access)</label>
                            <input type="text" id="raPort" value="${serverConfig?.ra_port || '3443'}">
                        </div>
                    </div>
                    
                    <div class="settings-actions">
                        <button class="btn btn-primary" onclick="testDatabaseConnection()">
                            <i class="fas fa-plug"></i> Probar Conexión
                        </button>
                        <button class="btn btn-secondary" onclick="saveServerConfig()">
                            <i class="fas fa-save"></i> Guardar Configuración
                        </button>
                    </div>
                    
                    <div id="connectionResult" style="margin-top: 20px;"></div>
                </div>
            </div>
        `
    };
    
    return modules[moduleName] || `
        <div style="text-align: center; padding: 50px;">
            <i class="fas fa-tools" style="font-size: 3rem; color: var(--accent-blue);"></i>
            <h3 style="margin: 20px 0 10px; color: var(--text-primary);">Módulo en Desarrollo</h3>
            <p style="color: var(--text-secondary);">El módulo "${getModuleTitle(moduleName)}" está en desarrollo.</p>
        </div>
    `;
}

function getModuleTitle(moduleName) {
    const titles = {
        dashboard: 'Panel de Control',
        servers: 'Estado de Servidores',
        accounts: 'Gestión de Cuentas',
        players: 'Gestion de Jugadores',
        realms: 'Gestión de Reinós',
        commands: 'Comandos en Tiempo Real',
        items: 'Gestión de Items',
        bans: 'Sistema de Bans',
        logs: 'Logs y Auditoría',
        achievements: 'Gestión de Logros',
        tickets: 'Sistema de Tickets',
        quests: 'Quest System',
        events: 'Sistema de Eventos',
        notifications: 'Notificaciones Push',
        settings: 'Configuración'
    };
    return titles[moduleName] || moduleName;
}

function initModule(moduleName) {
    switch(moduleName) {
        case 'dashboard':
            initDashboard();
            break;
        case 'accounts':
            initAccounts();
            break;
        case 'players':
            initPlayers();
            break;
        case 'settings':
            initSettings();
            break;
    }
}

function initDashboard() {
    updateDashboardStats();
    setupDashboardCards();
}

function initAccounts() {
    // Configurar búsqueda de cuentas
    const searchInput = document.getElementById('accountSearch');
    if (searchInput) {
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                searchAccounts(this.value);
            }
        });
    }
}

function initPlayers() {
    // Configurar búsqueda de jugadores
    const searchInput = document.getElementById('playerSearch');
    if (searchInput) {
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                searchPlayer();
            }
        });
    }
}

function initSettings() {
    // Cargar configuración actual
    if (serverConfig) {
        document.getElementById('serverIp').value = serverConfig.ip;
        document.getElementById('authDb').value = serverConfig.databases.auth;
        document.getElementById('charsDb').value = serverConfig.databases.characters;
        document.getElementById('worldDb').value = serverConfig.databases.world;
    }
}

function setupEvents() {
    // Actualizar conexión cada 30 segundos
    setInterval(checkConnection, 30000);
}

function checkConnection() {
    if (typeof Android !== 'undefined' && Android.testDatabaseConnection) {
        try {
            const result = JSON.parse(Android.testDatabaseConnection());
            updateConnectionStatus(result.success);
            
            if (result.success && currentModule === 'dashboard') {
                updateDashboardStats();
            }
        } catch (error) {
            updateConnectionStatus(false);
        }
    } else {
        updateConnectionStatus(false);
    }
}

function updateConnectionStatus(connected) {
    const statusDot = document.querySelector('.status-dot');
    const statusText = document.querySelector('.connection-status span:nth-child(2)');
    const sidebarStatus = document.querySelector('.status-indicator');
    
    if (connected) {
        statusDot.classList.remove('offline');
        statusDot.classList.add('online');
        statusText.textContent = 'Online';
        sidebarStatus.classList.remove('offline');
        sidebarStatus.classList.add('online');
        document.querySelector('.server-status span:nth-child(2)').textContent = 'Conectado';
    } else {
        statusDot.classList.remove('online');
        statusDot.classList.add('offline');
        statusText.textContent = 'Offline';
        sidebarStatus.classList.remove('online');
        sidebarStatus.classList.add('offline');
        document.querySelector('.server-status span:nth-child(2)').textContent = 'Sin conexión';
    }
}

function updateDashboardStats() {
    if (!serverConfig || !serverConfig.connected) {
        document.getElementById('serverStatus').textContent = 'Offline';
        document.getElementById('totalAccounts').textContent = '0';
        document.getElementById('onlinePlayers').textContent = '0';
        document.getElementById('activeRealms').textContent = '0';
        return;
    }
    
    // Aquí irían las llamadas a la base de datos para obtener estadísticas reales
    // Por ahora, mostramos datos de ejemplo
    document.getElementById('serverStatus').innerHTML = 
        '<span style="color: var(--success);">Online</span>';
    document.getElementById('totalAccounts').textContent = '1,245';
    document.getElementById('onlinePlayers').textContent = '128';
    document.getElementById('activeRealms').textContent = '2';
}

function setupDashboardCards() {
    const cards = document.querySelectorAll('.module-card');
    cards.forEach(card => {
        card.addEventListener('click', function() {
            const title = this.querySelector('.module-title').textContent;
            const moduleMap = {
                'Estado de Servidores': 'servers',
                'Gestión de Cuentas': 'accounts',
                'Gestión de Jugadores': 'players',
                'Gestión de Reinós': 'realms',
                'Comandos en Tiempo Real': 'commands',
                'Gestión de Items': 'items'
            };
            
            if (moduleMap[title]) {
                const navItem = document.querySelector(`[data-module="${moduleMap[title]}"]`);
                if (navItem) {
                    navItem.click();
                }
            }
        });
    });
}

// Funciones de gestión de cuentas
function searchAccounts(query) {
    if (!query.trim()) {
        alert('Ingresa un término de búsqueda');
        return;
    }
    
    if (!serverConfig || !serverConfig.connected) {
        alert('No hay conexión a la base de datos');
        return;
    }
    
    // Aquí iría la lógica para buscar cuentas en la base de datos
    console.log('Buscando cuentas:', query);
}

function showCreateAccountModal() {
    const modalHtml = `
        <div id="createAccountModal" style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; 
              background: rgba(0, 0, 0, 0.8); display: flex; align-items: center; justify-content: center; z-index: 1000;">
            <div style="background: var(--card-bg); border: 1px solid var(--border-color); 
                  border-radius: 8px; padding: 30px; width: 90%; max-width: 500px;">
                <h3 style="color: var(--accent-blue); margin-bottom: 20px;">
                    <i class="fas fa-user-plus"></i> Crear Nueva Cuenta
                </h3>
                
                <div class="input-group">
                    <label>Nombre de Usuario</label>
                    <input type="text" id="newUsername" placeholder="Ingresa nombre de usuario">
                </div>
                
                <div class="input-group">
                    <label>Contraseña</label>
                    <input type="password" id="newPassword" placeholder="Ingresa contraseña">
                </div>
                
                <div class="input-group">
                    <label>Confirmar Contraseña</label>
                    <input type="password" id="confirmPassword" placeholder="Confirma la contraseña">
                </div>
                
                <div class="input-group">
                    <label>Email</label>
                    <input type="email" id="newEmail" placeholder="ejemplo@correo.com">
                </div>
                
                <div class="input-group">
                    <label>Nivel de GM</label>
                    <select id="newGMLevel">
                        <option value="0">Jugador (0)</option>
                        <option value="1">Moderador (1)</option>
                        <option value="2">Game Master (2)</option>
                        <option value="3">Administrador (3)</option>
                    </select>
                </div>
                
                <div style="display: flex; gap: 10px; margin-top: 30px;">
                    <button class="btn btn-primary" onclick="createAccount()" style="flex: 1;">
                        <i class="fas fa-save"></i> Crear Cuenta
                    </button>
                    <button class="btn btn-secondary" onclick="closeModal()" style="flex: 1;">
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                </div>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

function closeModal() {
    const modal = document.getElementById('createAccountModal');
    if (modal) {
        modal.remove();
    }
}

function createAccount() {
    const username = document.getElementById('newUsername').value;
    const password = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const email = document.getElementById('newEmail').value;
    const gmLevel = document.getElementById('newGMLevel').value;
    
    if (!username || !password || !confirmPassword || !email) {
        alert('Todos los campos son obligatorios');
        return;
    }
    
    if (password !== confirmPassword) {
        alert('Las contraseñas no coinciden');
        return;
    }
    
    // Aquí iría la lógica para crear la cuenta en la base de datos
    console.log('Creando cuenta:', { username, email, gmLevel });
    
    if (typeof Android !== 'undefined' && Android.showToast) {
        Android.showToast('Cuenta creada exitosamente');
    }
    
    closeModal();
    refreshAccounts();
}

function refreshAccounts() {
    console.log('Actualizando lista de cuentas...');
    // Aquí iría la lógica para cargar cuentas desde la base de datos
}

// Funciones de gestión de jugadores
function searchPlayer() {
    const query = document.getElementById('playerSearch').value;
    
    if (!query.trim()) {
        alert('Ingresa el nombre de un personaje');
        return;
    }
    
    if (!serverConfig || !serverConfig.connected) {
        // Mostrar datos de ejemplo cuando no hay conexión
        const exampleData = [
            { name: 'Arthas', race: 'Humano', class: 'Paladin', level: '80', realm: 'Lordaeron', status: 'Online' },
            { name: 'Thrall', race: 'Orco', class: 'Shaman', level: '80', realm: 'Orgrimmar', status: 'Offline' },
            { name: 'Jaina', race: 'Humano', class: 'Mage', level: '80', realm: 'Lordaeron', status: 'Online' },
            { name: 'Sylvanas', race: 'Elfo Nocturno', class: 'Hunter', level: '80', realm: 'Lordaeron', status: 'Online' }
        ];
        
        displayPlayerResults(exampleData);
        return;
    }
    
    // Aquí iría la lógica real de búsqueda
    console.log('Buscando personaje:', query);
}

function displayPlayerResults(players) {
    const tbody = document.getElementById('playersTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    players.forEach(player => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>${player.name}</strong></td>
            <td>${player.race}</td>
            <td>${player.class}</td>
            <td>${player.level}</td>
            <td>${player.realm}</td>
            <td>
                <span class="status-badge ${player.status === 'Online' ? 'status-online' : 'status-offline'}">
                    ${player.status}
                </span>
            </td>
            <td>
                <button class="btn btn-secondary" onclick="viewPlayerDetails('${player.name}')" style="padding: 5px 10px; font-size: 0.9rem;">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-primary" onclick="editPlayer('${player.name}')" style="padding: 5px 10px; font-size: 0.9rem;">
                    <i class="fas fa-edit"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Funciones de configuración
function testDatabaseConnection() {
    if (typeof Android !== 'undefined' && Android.testDatabaseConnection) {
        const resultDiv = document.getElementById('connectionResult');
        resultDiv.innerHTML = `
            <div style="text-align: center; padding: 20px; background: var(--card-bg); border-radius: 5px;">
                <div class="spinner" style="width: 30px; height: 30px; margin: 0 auto 10px;"></div>
                <p>Probando conexión...</p>
            </div>
        `;
        
        setTimeout(() => {
            try {
                const result = JSON.parse(Android.testDatabaseConnection());
                
                if (result.success) {
                    resultDiv.innerHTML = `
                        <div style="background: rgba(76, 175, 80, 0.2); border: 1px solid var(--success); 
                              border-radius: 5px; padding: 15px; color: var(--success);">
                            <i class="fas fa-check-circle"></i> 
                            <strong>Conexión exitosa</strong>
                            <p style="margin-top: 5px; font-size: 0.9rem;">${result.message}</p>
                            <p style="margin-top: 10px; font-size: 0.8rem;">
                                Bases de datos encontradas: ${result.databases ? result.databases.length : 0}
                            </p>
                        </div>
                    `;
                    serverConfig.connected = true;
                    updateConnectionStatus(true);
                } else {
                    resultDiv.innerHTML = `
                        <div style="background: rgba(244, 67, 54, 0.2); border: 1px solid var(--danger); 
                              border-radius: 5px; padding: 15px; color: var(--danger);">
                            <i class="fas fa-times-circle"></i> 
                            <strong>Error de conexión</strong>
                            <p style="margin-top: 5px; font-size: 0.9rem;">${result.message}</p>
                        </div>
                    `;
                    serverConfig.connected = false;
                    updateConnectionStatus(false);
                }
            } catch (error) {
                resultDiv.innerHTML = `
                    <div style="background: rgba(244, 67, 54, 0.2); border: 1px solid var(--danger); 
                          border-radius: 5px; padding: 15px; color: var(--danger);">
                        <i class="fas fa-exclamation-triangle"></i> 
                        <strong>Error</strong>
                        <p style="margin-top: 5px; font-size: 0.9rem;">${error.message}</p>
                    </div>
                `;
            }
        }, 1500);
    } else {
        alert('Función no disponible');
    }
}

function saveServerConfig() {
    const config = {
        ip: document.getElementById('serverIp').value,
        port: document.getElementById('serverPort').value || '3306',
        auth_db: document.getElementById('authDb').value,
        chars_db: document.getElementById('charsDb').value,
        world_db: document.getElementById('worldDb').value,
        soap_port: document.getElementById('soapPort').value || '7878',
        ra_port: document.getElementById('raPort').value || '3443'
    };
    
    if (typeof Android !== 'undefined' && Android.saveServerConfig) {
        Android.saveServerConfig(JSON.stringify(config));
        serverConfig = {
            ...config,
            databases: {
                auth: config.auth_db,
                characters: config.chars_db,
                world: config.world_db
            },
            connected: serverConfig ? serverConfig.connected : false
        };
        
        if (typeof Android !== 'undefined' && Android.showToast) {
            Android.showToast('Configuración guardada');
        }
    } else {
        alert('Función no disponible');
    }
}

// Funciones auxiliares
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.innerHTML = `
        <div style="background: ${type === 'error' ? 'var(--danger)' : type === 'success' ? 'var(--success)' : 'var(--accent-blue)'}; 
              color: white; padding: 15px; border-radius: 5px; margin-bottom: 10px; 
              display: flex; align-items: center; gap: 10px;">
            <i class="fas fa-${type === 'error' ? 'exclamation-triangle' : type === 'success' ? 'check-circle' : 'info-circle'}"></i>
            ${message}
        </div>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

// Exportar funciones globales
window.loadModule = loadModule;
window.checkConnection = checkConnection;
window.testDatabaseConnection = testDatabaseConnection;
window.saveServerConfig = saveServerConfig;
window.showCreateAccountModal = showCreateAccountModal;
window.closeModal = closeModal;
window.createAccount = createAccount;
window.refreshAccounts = refreshAccounts;
window.searchPlayer = searchPlayer;
window.viewPlayerDetails = function(name) {
    alert(`Ver detalles de: ${name}`);
};
window.editPlayer = function(name) {
    alert(`Editar personaje: ${name}`);
};