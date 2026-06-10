const API = window.location.hostname === 'localhost' ? '' : 'https://TU_APP.railway.app';
let currentPage = 0;
const PAGE_SIZE = 20;
let favorites = new Set();
let teams = [];
let currentPokemon = null;

// ── Navigation ──────────────────────────────────────────────
document.querySelectorAll('.nav-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    btn.classList.add('active');
    const section = btn.dataset.section;
    document.getElementById(section).classList.add('active');
    if (section === 'favorites') loadFavorites();
    if (section === 'teams') loadTeams();
  });
});

// ── Toast ────────────────────────────────────────────────────
function toast(msg, error = false) {
  const t = document.createElement('div');
  t.className = 'toast' + (error ? ' error' : '');
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 2600);
}

// ── Loading ──────────────────────────────────────────────────
function loading(containerId) {
  document.getElementById(containerId).innerHTML =
    `<div class="loading"><div class="spinner"></div>Cargando...</div>`;
}

// ── Type colors ──────────────────────────────────────────────
function typeBadge(type) {
  return `<span class="type-badge ${type}">${type}</span>`;
}

// ── Pokemon Card ─────────────────────────────────────────────
function pokemonCard(p, showFav = true) {
  const isFav = favorites.has(p.name);
  return `
    <div class="card" onclick="openModal('${p.name}')">
      ${showFav ? `<button class="fav-btn ${isFav ? 'active' : ''}" title="Favorito"
        onclick="toggleFavorite(event,'${p.name}')">★</button>` : ''}
      <img src="${p.sprite || 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png'}"
           alt="${p.name}" onerror="this.src='https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png'" />
      <div class="name">${p.name}</div>
      <div class="id">#${String(p.id).padStart(3,'0')}</div>
    </div>`;
}

// ── Browse ───────────────────────────────────────────────────
async function loadPage(page) {
  loading('pokemon-grid');
  const offset = page * PAGE_SIZE;
  try {
    const data = await fetch(`${API}/api/pokemon?offset=${offset}&limit=${PAGE_SIZE}`).then(r => r.json());
    document.getElementById('pokemon-grid').innerHTML = data.map(p => pokemonCard(p)).join('');
    document.getElementById('page-info').textContent = `Página ${page + 1}`;
    document.getElementById('prev-btn').disabled = page === 0;
    currentPage = page;
  } catch {
    document.getElementById('pokemon-grid').innerHTML = '<p class="empty-msg">Error al cargar. ¿Está corriendo el servidor?</p>';
  }
}

document.getElementById('prev-btn').addEventListener('click', () => loadPage(currentPage - 1));
document.getElementById('next-btn').addEventListener('click', () => loadPage(currentPage + 1));

document.getElementById('search-btn').addEventListener('click', searchPokemon);
document.getElementById('search-input').addEventListener('keydown', e => { if (e.key === 'Enter') searchPokemon(); });

async function searchPokemon() {
  const name = document.getElementById('search-input').value.trim().toLowerCase();
  if (!name) { loadPage(0); return; }
  loading('pokemon-grid');
  document.querySelector('.pagination').style.display = 'none';
  try {
    const p = await fetch(`${API}/api/pokemon/${name}`).then(r => { if (!r.ok) throw r; return r.json(); });
    const summary = { id: p.id, name: p.name, sprite: p.sprite };
    document.getElementById('pokemon-grid').innerHTML = pokemonCard(summary);
  } catch {
    document.getElementById('pokemon-grid').innerHTML = `<p class="empty-msg">Pokémon "${name}" no encontrado.</p>`;
  }
}

document.getElementById('search-input').addEventListener('input', e => {
  if (!e.target.value.trim()) {
    document.querySelector('.pagination').style.display = 'flex';
    loadPage(0);
  }
});

// ── Favorites ────────────────────────────────────────────────
async function toggleFavorite(event, name) {
  event.stopPropagation();
  if (favorites.has(name)) {
    await fetch(`${API}/api/favorites/${name}`, { method: 'DELETE' });
    favorites.delete(name);
    toast(`${name} eliminado de favoritos`);
  } else {
    await fetch(`${API}/api/favorites/${name}`, { method: 'POST' });
    favorites.add(name);
    toast(`★ ${name} añadido a favoritos`);
  }
  document.querySelectorAll('.fav-btn').forEach(btn => {
    if (btn.closest('.card') && btn.closest('.card').querySelector('.name')?.textContent === name) {
      btn.classList.toggle('active', favorites.has(name));
    }
  });
}

async function loadFavorites() {
  loading('favorites-grid');
  try {
    const data = await fetch(`${API}/api/favorites`).then(r => r.json());
    favorites = new Set(data.map(p => p.name));
    const empty = document.getElementById('favorites-empty');
    if (!data.length) {
      document.getElementById('favorites-grid').innerHTML = '';
      empty.style.display = 'block';
    } else {
      empty.style.display = 'none';
      document.getElementById('favorites-grid').innerHTML = data.map(p => pokemonCard(p)).join('');
    }
  } catch {
    document.getElementById('favorites-grid').innerHTML = '<p class="empty-msg">Error al cargar favoritos.</p>';
  }
}

// ── Teams ────────────────────────────────────────────────────
async function loadTeams() {
  try {
    teams = await fetch(`${API}/api/teams`).then(r => r.json());
    renderTeams();
  } catch {
    document.getElementById('teams-list').innerHTML = '<p class="empty-msg">Error al cargar equipos.</p>';
  }
}

function renderTeams() {
  const empty = document.getElementById('teams-empty');
  const list = document.getElementById('teams-list');
  if (!teams.length) { list.innerHTML = ''; empty.style.display = 'block'; return; }
  empty.style.display = 'none';
  list.innerHTML = teams.map(team => `
    <div class="team-card">
      <div class="team-card-header">
        <h3>⚔️ ${team.name} <span style="color:var(--muted);font-size:0.8rem">(${team.members.length}/6)</span></h3>
        <button class="delete-team-btn" onclick="deleteTeam('${team.id}')">🗑️ Eliminar</button>
      </div>
      <div class="team-members">
        ${team.members.map(m => `
          <div class="team-member">
            <button class="remove-member-btn" onclick="removeMember('${team.id}','${m.name}')">✕</button>
            <img src="${m.sprite}" alt="${m.name}" />
            <div class="name">${m.name}</div>
          </div>`).join('')}
        ${team.members.length < 6 ? `
          <div class="team-add-input">
            <input id="add-input-${team.id}" type="text" placeholder="Añadir Pokémon..." />
            <button onclick="addToTeam('${team.id}')">+</button>
          </div>` : ''}
      </div>
    </div>`).join('');
}

document.getElementById('create-team-btn').addEventListener('click', async () => {
  const name = document.getElementById('team-name-input').value.trim();
  if (!name) { toast('Escribe un nombre para el equipo', true); return; }
  try {
    const team = await fetch(`${API}/api/teams`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    }).then(r => r.json());
    teams.push(team);
    renderTeams();
    document.getElementById('team-name-input').value = '';
    toast(`Equipo "${name}" creado`);
  } catch { toast('Error al crear el equipo', true); }
});

async function deleteTeam(id) {
  await fetch(`${API}/api/teams/${id}`, { method: 'DELETE' });
  teams = teams.filter(t => t.id !== id);
  renderTeams();
  toast('Equipo eliminado');
}

async function addToTeam(teamId) {
  const input = document.getElementById(`add-input-${teamId}`);
  const name = input.value.trim().toLowerCase();
  if (!name) return;
  try {
    const team = await fetch(`${API}/api/teams/${teamId}/pokemon/${name}`, { method: 'POST' })
      .then(r => { if (!r.ok) throw r; return r.json(); });
    teams = teams.map(t => t.id === teamId ? team : t);
    renderTeams();
    toast(`${name} añadido al equipo`);
  } catch { toast(`No se pudo añadir ${name}`, true); }
}

async function removeMember(teamId, pokemonName) {
  const team = await fetch(`${API}/api/teams/${teamId}/pokemon/${pokemonName}`, { method: 'DELETE' }).then(r => r.json());
  teams = teams.map(t => t.id === teamId ? team : t);
  renderTeams();
  toast(`${pokemonName} eliminado del equipo`);
}

// ── Modal ────────────────────────────────────────────────────
async function openModal(name) {
  const modal = document.getElementById('modal');
  document.getElementById('modal-body').innerHTML = '<div class="loading"><div class="spinner"></div>Cargando...</div>';
  modal.classList.remove('hidden');
  try {
    const p = await fetch(`${API}/api/pokemon/${name}`).then(r => r.json());
    currentPokemon = p;
    const teamOptions = teams.map(t => `<option value="${t.id}">${t.name}</option>`).join('');
    document.getElementById('modal-body').innerHTML = `
      <img src="${p.sprite}" alt="${p.name}" />
      <h2>${p.name}</h2>
      <div class="modal-id">#${String(p.id).padStart(3,'0')}</div>
      <div class="types">${p.types.map(typeBadge).join('')}</div>
      <div class="modal-info">
        <div class="info-item"><div class="label">Altura</div><div class="val">${(p.height/10).toFixed(1)} m</div></div>
        <div class="info-item"><div class="label">Peso</div><div class="val">${(p.weight/10).toFixed(1)} kg</div></div>
        <div class="info-item"><div class="label">Exp. base</div><div class="val">${p.baseExperience}</div></div>
      </div>
      <div class="stats-grid">
        ${p.stats.map(s => `
          <div class="stat-row">
            <span class="stat-name">${s.name}</span>
            <div class="stat-bar-wrap"><div class="stat-bar" style="width:${Math.min(s.baseStat/255*100,100)}%"></div></div>
            <span class="stat-val">${s.baseStat}</span>
          </div>`).join('')}
      </div>
      ${teamOptions ? `
        <div class="add-team-select">
          <select id="modal-team-select">${teamOptions}</select>
          <button onclick="addCurrentToTeam()">+ Añadir al equipo</button>
        </div>` : ''}`;
  } catch {
    document.getElementById('modal-body').innerHTML = '<p class="empty-msg">Error al cargar el Pokémon.</p>';
  }
}

async function addCurrentToTeam() {
  if (!currentPokemon) return;
  const teamId = document.getElementById('modal-team-select').value;
  try {
    const team = await fetch(`${API}/api/teams/${teamId}/pokemon/${currentPokemon.name}`, { method: 'POST' })
      .then(r => { if (!r.ok) throw r; return r.json(); });
    teams = teams.map(t => t.id === teamId ? team : t);
    toast(`${currentPokemon.name} añadido al equipo "${team.name}"`);
  } catch { toast('No se pudo añadir (equipo lleno o ya existe)', true); }
}

document.getElementById('modal-close').addEventListener('click', () => document.getElementById('modal').classList.add('hidden'));
document.getElementById('modal').addEventListener('click', e => { if (e.target === document.getElementById('modal')) document.getElementById('modal').classList.add('hidden'); });

// ── Compare ──────────────────────────────────────────────────
document.getElementById('compare-btn').addEventListener('click', async () => {
  const names = [
    document.getElementById('compare-1').value.trim().toLowerCase(),
    document.getElementById('compare-2').value.trim().toLowerCase(),
    document.getElementById('compare-3').value.trim().toLowerCase()
  ].filter(Boolean);

  if (names.length < 2) { toast('Introduce al menos 2 Pokémon', true); return; }
  const result = document.getElementById('compare-result');
  result.innerHTML = '<div class="loading"><div class="spinner"></div>Comparando...</div>';

  try {
    const [cmp, ...details] = await Promise.all([
      fetch(`${API}/api/pokemon/compare?names=${names.join(',')}`).then(r => { if (!r.ok) throw r; return r.json(); }),
      ...names.map(n => fetch(`${API}/api/pokemon/${n}`).then(r => r.json()))
    ]);

    const headerCells = details.map(p => `
      <th class="compare-header">
        <img src="${p.sprite}" alt="${p.name}" /><div class="name">${p.name}</div>
      </th>`).join('');

    const statRows = Object.entries(cmp.stats).map(([stat, vals]) => {
      const cells = names.map(n => {
        const isWinner = cmp.winner[stat] === n;
        return `<td class="${isWinner ? 'winner' : ''}">${vals[n] ?? '-'}${isWinner ? ' 🏆' : ''}</td>`;
      }).join('');
      return `<tr><td class="stat-label">${stat}</td>${cells}</tr>`;
    }).join('');

    result.innerHTML = `
      <table class="compare-table">
        <thead><tr><th></th>${headerCells}</tr></thead>
        <tbody>${statRows}</tbody>
      </table>`;
  } catch { result.innerHTML = '<p class="empty-msg">Error al comparar. Verifica los nombres.</p>'; }
});

// ── Init ─────────────────────────────────────────────────────
loadPage(0);
fetch(`${API}/api/favorites`).then(r => r.json()).then(data => { favorites = new Set(data.map(p => p.name)); }).catch(() => {});
fetch(`${API}/api/teams`).then(r => r.json()).then(data => { teams = data; }).catch(() => {});
