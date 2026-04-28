/* ============================================================
   5THEWAY® OUTLET™ — main.js  v3.0
   Single-file bundle — all pages
   Architecture: IIFE core → page modules guarded by DOM checks
   ─────────────────────────────────────────────────────────────
   00 · Core utilities & shared UI       (all pages)
   01 · Category / listing pages         (productsGrid)
   02 · Cart page                        (cartForm)
   03 · Login / auth page                (loginPhone)
   04 · Orders page                      (ordersPage)
   05 · Profile page                     (profileMain)
   06 · Product detail page              (galleryMain)
   07 · Checkout page                    (checkoutMain)
   ============================================================ */

'use strict';

/* ============================================================
   00 · CORE — runs on every page
   ============================================================ */

/* ── Shared keyframes ─────────────────────────────────────── */
(function injectKeyframes() {
    if (document.getElementById('5tw-kf')) return;
    const s = document.createElement('style');
    s.id = '5tw-kf';
    s.textContent = `
    @keyframes fadeInUp {
      from { opacity:0; transform:translateY(20px); }
      to   { opacity:1; transform:translateY(0); }
    }
    @keyframes fadeInPD {
      from { opacity:0; }
      to   { opacity:1; }
    }
    @keyframes slideUpPD {
      from { opacity:0; transform:translateY(24px); }
      to   { opacity:1; transform:translateY(0); }
    }
    @keyframes shakeX {
      0%,100% { transform:translateX(0); }
      20%     { transform:translateX(-6px); }
      40%     { transform:translateX(6px); }
      60%     { transform:translateX(-4px); }
      80%     { transform:translateX(4px); }
    }
    @keyframes spin {
      from { transform:rotate(0deg); }
      to   { transform:rotate(360deg); }
    }
    @keyframes pdPulse {
      0%  { transform:scale(1); }
      50% { transform:scale(1.35); }
      100%{ transform:scale(1); }
    }
    @keyframes checkPop {
      0%   { transform:scale(0) rotate(-10deg); opacity:0; }
      70%  { transform:scale(1.15) rotate(3deg); }
      100% { transform:scale(1) rotate(0); opacity:1; }
    }
  `;
    document.head.appendChild(s);
}());

/* ── Utilities ────────────────────────────────────────────── */
const fmt = n => n.toLocaleString('vi') + 'đ';

function showToast(msg) {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.className = 'toast-5w';
        document.body.appendChild(toast);
    }
    toast.textContent = '✓ ' + msg;
    toast.classList.add('show');
    clearTimeout(toast._t);
    toast._t = setTimeout(() => toast.classList.remove('show'), 2400);
}

function togglePass(inputId, btn) {
    const inp = document.getElementById(inputId);
    if (!inp) return;
    const show = inp.type === 'password';
    inp.type = show ? 'text' : 'password';
    btn.style.color = show ? 'var(--black)' : 'var(--gray-muted)';
    btn.setAttribute('aria-label', show ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
    btn.innerHTML = show
        ? `<svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
         <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/>
         <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/>
         <line x1="1" y1="1" x2="23" y2="23"/></svg>`
        : `<svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
         <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
         <circle cx="12" cy="12" r="3"/></svg>`;
}

/* ── Cart badge ───────────────────────────────────────────── */
let _cartCount = 0;
function updateCartBadge(delta = 1) {
    _cartCount = Math.max(0, _cartCount + delta);
    document.querySelectorAll('.cart-badge').forEach(el => el.textContent = _cartCount);
}

/* ── Intersection observer — fade-in cards ───────────────── */
function observeCards(scope = document) {
    const cards = scope.querySelectorAll('.product-card, .product-feature-card');
    if (!cards.length) return;
    const io = new IntersectionObserver((entries, obs) => {
        entries.forEach(entry => {
            if (!entry.isIntersecting) return;
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
            obs.unobserve(entry.target);
        });
    }, { threshold: 0.08, rootMargin: '0px 0px -30px 0px' });
    cards.forEach((card, i) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(18px)';
        card.style.transition = `opacity 0.4s ease ${i * 0.045}s, transform 0.4s ease ${i * 0.045}s, box-shadow 0.3s`;
        io.observe(card);
    });
}

/* ── Expose global namespace ──────────────────────────────── */
window.FTW = { fmt, showToast, togglePass, updateCartBadge, observeCards };

/* ── Navbar — mobile menu & search ─────────────────────────── */
(function initNav() {
    const menuToggle = document.getElementById('menuToggle');
    const menuClose = document.getElementById('menuClose');
    const mobileMenu = document.getElementById('mobileMenu');
    const overlay = document.getElementById('overlay');
    const searchToggle = document.getElementById('searchToggle');
    const searchWrapper = document.getElementById('searchWrapper');
    const searchInput = document.getElementById('searchNavInput');
    const navbar = document.querySelector('.navbar-5w');

    const openMenu = () => { mobileMenu?.classList.add('open'); overlay?.classList.add('show'); document.body.style.overflow = 'hidden'; menuToggle?.setAttribute('aria-expanded', 'true'); };
    const closeMenu = () => { mobileMenu?.classList.remove('open'); overlay?.classList.remove('show'); document.body.style.overflow = ''; menuToggle?.setAttribute('aria-expanded', 'false'); };

    menuToggle?.addEventListener('click', openMenu);
    menuClose?.addEventListener('click', closeMenu);

    searchToggle?.addEventListener('click', function(e) {
        if (!searchWrapper?.classList.contains('active')) {
            e.preventDefault();
            searchWrapper?.classList.add('active');
            searchInput?.focus();
        } else {
            if (!searchInput?.value.trim()) {
                e.preventDefault();
                searchWrapper?.classList.remove('active');
            }
            // Nếu có dữ liệu, trình duyệt sẽ tự submit form vì nút là type="submit"
        }
    });

    // Đóng search khi click ra ngoài
    document.addEventListener('click', e => {
        if (searchWrapper?.classList.contains('active') && !searchWrapper.contains(e.target)) {
            searchWrapper.classList.remove('active');
        }
    });

    overlay?.addEventListener('click', closeMenu);
    document.addEventListener('keydown', e => { 
        if (e.key === 'Escape') { 
            closeMenu(); 
            searchWrapper?.classList.remove('active');
        } 
    });

    if (navbar) {
        window.addEventListener('scroll', () => {
            navbar.style.boxShadow = window.scrollY > 10 ? '0 2px 20px rgba(0,0,0,0.25)' : 'none';
        }, { passive: true });
    }
}());

/* ── Homepage category strip ──────────────────────────────── */
document.querySelectorAll('.category-pill').forEach(pill => {
    pill.addEventListener('click', function () {
        document.querySelectorAll('.category-pill').forEach(p => p.classList.remove('active'));
        this.classList.add('active');
    });
});

/* ── Homepage hero Swiper ─────────────────────────────────── */
if (document.querySelector('.heroSwiper') && typeof Swiper !== 'undefined') {
    new Swiper('.heroSwiper', {
        loop: true,
        autoplay: { delay: 5500, disableOnInteraction: false, pauseOnMouseEnter: true },
        effect: 'fade', fadeEffect: { crossFade: true }, speed: 800,
        pagination: { el: '.swiper-pagination', clickable: true },
        navigation: { nextEl: '.swiper-button-next', prevEl: '.swiper-button-prev' },
    });
}

/* ── Global add-to-cart feedback ──────────────────────────── */
document.addEventListener('click', e => {
    const btn = e.target.closest('.btn-add-cart');
    if (!btn) return;
    
    // Nếu là thẻ liên kết (<a>) thì để trình duyệt chuyển trang bình thường
    if (btn.tagName.toLowerCase() === 'a') return;

    // Nếu là nút bấm (button) thì mới xử lý AJAX add-to-cart
    e.preventDefault(); e.stopPropagation();
    const orig = btn.innerHTML;
    btn.innerHTML = `<svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg> Đã thêm!`;
    btn.style.cssText = 'background:#22c55e;color:#fff';
    updateCartBadge(1);
    setTimeout(() => { btn.innerHTML = orig; btn.style.cssText = ''; }, 1800);
});

/* ── Shared card observer on load ───────────────────── */
document.addEventListener('DOMContentLoaded', () => observeCards());

/* ── Global Filter Helpers (Mobile & Server-side) ────────── */
window.toggleFilterGroup = function(button) {
    const group = button.parentElement;
    group.classList.toggle('open');
};

window.selectColor = function(element) {
    const color = element.getAttribute('data-color');
    const hiddenInput = document.getElementById('hiddenColor');
    if (!hiddenInput) return;
    
    if (hiddenInput.value === color) {
        hiddenInput.value = "";
        element.classList.remove('active-border');
    } else {
        document.querySelectorAll('.color-swatch').forEach(el => el.classList.remove('active-border'));
        hiddenInput.value = color;
        element.classList.add('active-border');
    }
};

(function initSharedUI() {
    function initMobileFilter() {
        const toggleBtn = document.getElementById('filterToggle');
        const sidebar = document.getElementById('filterSidebar');
        const closeBtn = document.getElementById('filterClose');
        const overlay = document.getElementById('overlay');

        if (!toggleBtn || !sidebar) return;

        const openFilter = () => {
            sidebar.classList.add('active');
            toggleBtn.classList.add('active');
            if (overlay) overlay.classList.add('show');
            document.body.style.overflow = 'hidden';
        };

        const closeFilter = () => {
            sidebar.classList.remove('active');
            toggleBtn.classList.remove('active');
            if (overlay) overlay.classList.remove('show');
            document.body.style.overflow = '';
        };

        toggleBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            sidebar.classList.contains('active') ? closeFilter() : openFilter();
        });

        if (closeBtn) closeBtn.addEventListener('click', closeFilter);
        if (overlay) overlay.addEventListener('click', closeFilter);
        document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeFilter(); });
    }

    function initBackTop() {
        const btn = document.querySelector('.back-to-top');
        if (!btn) return;
        window.addEventListener('scroll', () => { btn.classList.toggle('show', window.scrollY > 400); }, { passive: true });
        btn.addEventListener('click', () => { window.scrollTo({ top: 0, behavior: 'smooth' }); });
    }

    document.addEventListener('DOMContentLoaded', () => {
        initMobileFilter();
        initBackTop();
    });
}());


/* ============================================================
   01 · CATEGORY PAGE  (guard: #productsGrid)
   ============================================================ */

if (document.getElementById('productsGrid')) (function initCategory() {

    const state = { subcategory: 'all', colors: [], sizes: [], priceMin: null, priceMax: null, sort: 'default', listView: false, loaded: 0, total: 0 };
    const $ = id => document.getElementById(id);
    const filterToggle = $('filterToggle');
    const filterSidebar = $('filterSidebar');
    const filterOverlay = $('filterOverlay');
    const catLayout = $('catLayout');
    const productsGrid = $('productsGrid');
    const sortSelect = $('sortSelect');
    const viewGrid = $('viewGrid');
    const viewList = $('viewList');
    const loadMoreBtn = $('loadMoreBtn');
    const clearAll = $('clearAll');
    const activeFiltersEl = $('activeFilters');
    const resultCount = $('resultCount');
    const priceMin = $('priceMin');
    const priceMax = $('priceMax');
    const isDesktop = () => window.innerWidth >= 992;
    const BATCH = 4;
    let hiddenCards = [];

    /* Sidebar */
    function openSidebar() { filterSidebar.classList.add('mobile-open'); filterOverlay?.classList.add('show'); filterToggle.setAttribute('aria-expanded', 'true'); if (!isDesktop()) document.body.style.overflow = 'hidden'; }
    function closeSidebar() { filterSidebar.classList.remove('mobile-open'); filterOverlay?.classList.remove('show'); filterToggle.classList.remove('active'); filterToggle.setAttribute('aria-expanded', 'false'); document.body.style.overflow = ''; if (isDesktop()) { filterSidebar.classList.add('hidden-desktop'); catLayout?.classList.add('sidebar-hidden'); } }
    function toggleSidebarDesktop() { const h = filterSidebar.classList.contains('hidden-desktop'); filterSidebar.classList.toggle('hidden-desktop', !h); catLayout?.classList.toggle('sidebar-hidden', !h); filterToggle.classList.toggle('active', h); filterToggle.setAttribute('aria-expanded', String(h)); }
    function initSidebar() { if (isDesktop()) { filterSidebar.classList.remove('hidden-desktop'); catLayout?.classList.remove('sidebar-hidden'); filterToggle.classList.add('active'); filterToggle.setAttribute('aria-expanded', 'true'); } }

    filterToggle?.addEventListener('click', () => isDesktop() ? toggleSidebarDesktop() : openSidebar());
    filterOverlay?.addEventListener('click', closeSidebar);
    $('applyFilter')?.addEventListener('click', closeSidebar);
    $('resetFilter')?.addEventListener('click', () => { resetFilters(); closeSidebar(); });

    /* Accordion */
    document.querySelectorAll('.filter-group').forEach(g => {
        g.querySelector('.filter-group__trigger')?.addEventListener('click', () => { const open = g.classList.toggle('open'); g.querySelector('.filter-group__trigger')?.setAttribute('aria-expanded', String(open)); });
    });

    /* Chips */
    document.querySelectorAll('.cat-chip').forEach(c => c.addEventListener('click', function () { document.querySelectorAll('.cat-chip').forEach(x => x.classList.remove('active')); this.classList.add('active'); state.subcategory = this.dataset.filter; applyFilters(); }));
    document.querySelectorAll('.color-swatch').forEach(sw => sw.addEventListener('click', function () { this.classList.toggle('active'); const col = this.title; state.colors = this.classList.contains('active') ? [...state.colors, col] : state.colors.filter(c => c !== col); renderTags(); }));
    document.querySelectorAll('.size-chip:not(.out)').forEach(c => c.addEventListener('click', function () { this.classList.toggle('active'); const sz = this.textContent.trim(); state.sizes = this.classList.contains('active') ? [...state.sizes, sz] : state.sizes.filter(s => s !== sz); renderTags(); }));
    document.querySelectorAll('.filter-cat-list a').forEach(l => l.addEventListener('click', e => { e.preventDefault(); document.querySelectorAll('.filter-cat-list a').forEach(x => x.classList.remove('active')); l.classList.add('active'); state.subcategory = l.dataset.filter || 'all'; applyFilters(); }));

    /* Price debounce */
    let priceTimer;
    [priceMin, priceMax].forEach(inp => inp?.addEventListener('input', () => { clearTimeout(priceTimer); priceTimer = setTimeout(() => { state.priceMin = priceMin?.value ? parseInt(priceMin.value) : null; state.priceMax = priceMax?.value ? parseInt(priceMax.value) : null; renderTags(); }, 300); }));

    /* Sort & view */
    sortSelect?.addEventListener('change', function () { state.sort = this.value; sortProducts(); });
    viewGrid?.addEventListener('click', () => setView(false));
    viewList?.addEventListener('click', () => setView(true));
    function setView(list) { state.listView = list; viewGrid?.classList.toggle('active', !list); viewList?.classList.toggle('active', list); productsGrid?.classList.toggle('list-view', list); productsGrid?.classList.toggle('grid-4', !list); }

    /* Filter */
    function applyFilters() {
        let count = 0;
        productsGrid?.querySelectorAll('.product-card[data-category]').forEach(card => {
            let show = true;
            if (state.subcategory !== 'all' && card.dataset.category !== state.subcategory) show = false;
            if (show && (state.priceMin || state.priceMax)) { const p = parseInt(card.dataset.price || 0); if (state.priceMin && p < state.priceMin) show = false; if (state.priceMax && p > state.priceMax) show = false; }
            card.style.display = show ? '' : 'none';
            if (show) count++;
        });
        const label = count + ' sản phẩm';
        if (resultCount) resultCount.textContent = label;
        const tc = $('totalCount'); if (tc) tc.textContent = label;
    }

    /* Sort */
    function sortProducts() {
        if (!productsGrid) return;
        [...productsGrid.querySelectorAll('.product-card')].sort((a, b) => {
            if (state.sort === 'price-asc') return parseInt(a.dataset.price || 0) - parseInt(b.dataset.price || 0);
            if (state.sort === 'price-desc') return parseInt(b.dataset.price || 0) - parseInt(a.dataset.price || 0);
            if (state.sort === 'name-asc') return (a.querySelector('.product-card__name')?.textContent || '').localeCompare(b.querySelector('.product-card__name')?.textContent || '', 'vi');
            return 0;
        }).forEach(c => productsGrid.appendChild(c));
    }

    /* Reset */
    function resetFilters() {
        Object.assign(state, { subcategory: 'all', colors: [], sizes: [], priceMin: null, priceMax: null });
        document.querySelectorAll('.cat-chip').forEach(c => c.classList.toggle('active', c.dataset.filter === 'all'));
        document.querySelectorAll('.color-swatch,.size-chip').forEach(el => el.classList.remove('active'));
        if (priceMin) priceMin.value = ''; if (priceMax) priceMax.value = '';
        renderTags();
    }
    clearAll?.addEventListener('click', resetFilters);

    /* Filter tags */
    function renderTags() {
        applyFilters();
        if (!activeFiltersEl) return;
        const tags = [...state.colors.map(c => ({ label: c, type: 'color', key: c })), ...state.sizes.map(s => ({ label: `SIZE: ${s}`, type: 'size', key: s })), ...(state.priceMin ? [{ label: `Từ: ${state.priceMin.toLocaleString('vi')}đ`, type: 'priceMin', key: '' }] : []), ...(state.priceMax ? [{ label: `Đến: ${state.priceMax.toLocaleString('vi')}đ`, type: 'priceMax', key: '' }] : [])];
        activeFiltersEl.style.display = tags.length ? 'flex' : 'none';
        activeFiltersEl.innerHTML = tags.map(t => `<span class="active-filter-tag">${t.label}<button aria-label="Xoá ${t.label}" data-type="${t.type}" data-key="${t.key}">×</button></span>`).join('');
    }
    activeFiltersEl?.addEventListener('click', e => {
        const btn = e.target.closest('button[data-type]'); if (!btn) return;
        const { type, key } = btn.dataset;
        if (type === 'color') { state.colors = state.colors.filter(c => c !== key); document.querySelectorAll(`.color-swatch[title="${key}"]`).forEach(sw => sw.classList.remove('active')); }
        else if (type === 'size') { state.sizes = state.sizes.filter(s => s !== key); document.querySelectorAll('.size-chip').forEach(c => { if (c.textContent.trim() === key) c.classList.remove('active'); }); }
        else if (type === 'priceMin') { state.priceMin = null; if (priceMin) priceMin.value = ''; }
        else if (type === 'priceMax') { state.priceMax = null; if (priceMax) priceMax.value = ''; }
        renderTags();
    });

    /* Load more */
    function initLoadMore() {
        const all = [...(productsGrid?.querySelectorAll('.product-card') || [])];
        state.total = all.length; state.loaded = 0; hiddenCards = [];
        const initial = parseInt(productsGrid?.dataset.initial || 10);
        all.forEach((c, i) => { if (i < initial) { state.loaded++; } else { c.style.display = 'none'; hiddenCards.push(c); } });
        updateLoadMoreUI();
    }
    function updateLoadMoreUI() {
        const fill = document.querySelector('.load-progress__fill');
        const meta = document.querySelector('.load-meta');
        if (fill) fill.style.width = `${state.total ? (state.loaded / state.total) * 100 : 0}%`;
        if (meta) meta.textContent = `Đang xem ${state.loaded} / ${state.total} sản phẩm`;
        if (!loadMoreBtn) return;
        const rem = hiddenCards.length; loadMoreBtn.style.display = rem <= 0 ? 'none' : '';
        if (rem > 0) loadMoreBtn.innerHTML = `<svg width="15" height="15" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.51"/></svg> Tải thêm ${Math.min(rem, BATCH)} sản phẩm`;
    }
    loadMoreBtn?.addEventListener('click', () => {
        hiddenCards.splice(0, BATCH).forEach(c => { c.style.display = ''; c.style.animation = 'fadeInUp 0.4s ease both'; state.loaded++; });
        updateLoadMoreUI(); FTW.observeCards(productsGrid);
    });

    document.addEventListener('DOMContentLoaded', () => { initSidebar(); initLoadMore(); FTW.observeCards(productsGrid); });

}());


/* ============================================================
   02 · CART PAGE  (guard: #cartForm or cart items)
   ============================================================ */

if (document.getElementById('ci-1') || document.querySelector('.cart-item')) (function initCart() {

    const prices = { 'ci-1': 390000, 'ci-2': 590000, 'ci-3': 220000 };
    const qtys = { 'ci-1': 1, 'ci-2': 2, 'ci-3': 1 };
    let discountPct = 0;
    const FREE_SHIP = 500000, SHIP_COST = 30000;
    const VOUCHERS = { '5TW10': { pct: 10, label: 'giảm 10%' }, 'SALE20': { pct: 20, label: 'giảm 20%' }, 'FREESHIP': { pct: 0, label: 'miễn phí vận chuyển' } };
    const $ = id => document.getElementById(id);

    function updateTotals() {
        let sub = 0;
        Object.keys(qtys).forEach(id => { const row = $(id); if (!row || row.style.display === 'none') return; const lt = prices[id] * qtys[id]; const pe = $('price-' + id); if (pe) pe.textContent = FTW.fmt(lt); sub += lt; });
        const ship = sub >= FREE_SHIP ? 0 : SHIP_COST, disc = Math.round(sub * discountPct / 100);
        const sub_el = $('subtotal'); if (sub_el) sub_el.textContent = FTW.fmt(sub);
        const tot_el = $('totalAmount'); if (tot_el) tot_el.textContent = FTW.fmt(sub + ship - disc);
        const dr = $('discountRow'); if (dr) dr.style.display = disc > 0 ? 'flex' : 'none';
        const dv = $('discountVal'); if (dv && disc > 0) dv.textContent = '−' + FTW.fmt(disc);
        // Ship bar
        const sn = $('shipNotice'); if (!sn) return;
        if (ship === 0) { sn.style.display = 'none'; } else { sn.style.display = ''; const pct = Math.min(100, (sub / FREE_SHIP) * 100); const bf = document.querySelector('.ship-bar-fill'); if (bf) bf.style.width = pct + '%'; const txt = sn.querySelector('p'); if (txt) txt.innerHTML = `Thêm <b>${FTW.fmt(FREE_SHIP - sub)}</b> để được FREE SHIP!`; }
    }

    function changeQty(id, delta) { qtys[id] = Math.max(1, (qtys[id] || 1) + delta); const qe = $('qty-' + id); if (qe) qe.textContent = qtys[id]; const row = $(id); if (row) row.querySelector('[data-action="dec"]')?.toggleAttribute('disabled', qtys[id] <= 1); updateTotals(); }
    function removeItem(id) { const el = $(id); if (!el) return; el.style.transition = 'opacity .3s,transform .3s'; el.style.opacity = '0'; el.style.transform = 'translateX(-20px)'; setTimeout(() => { el.style.display = 'none'; updateTotals(); }, 320); }

    function applyVoucher() {
        const inp = $('voucherInput'); const msg = $('voucherMsg'); const code = inp?.value.trim().toUpperCase() || ''; const v = VOUCHERS[code];
        if (v !== undefined) { discountPct = v.pct; if (msg) { msg.style.display = 'block'; msg.style.color = '#15803d'; msg.textContent = `✓ Áp dụng mã "${code}" — ${v.label}`; } }
        else { if (msg) { msg.style.display = 'block'; msg.style.color = 'var(--price-red)'; msg.textContent = '✗ Mã không hợp lệ hoặc đã hết hạn'; } }
        updateTotals();
    }

    // Select all
    function initSelectAll() {
        const sa = $('selectAll'); if (!sa) return;
        sa.addEventListener('change', function () { document.querySelectorAll('.item-check').forEach(cb => cb.checked = this.checked); });
        document.querySelectorAll('.item-check').forEach(cb => cb.addEventListener('change', () => { const all = document.querySelectorAll('.item-check'), chk = document.querySelectorAll('.item-check:checked'); if (sa) sa.checked = all.length === chk.length; }));
    }

    document.addEventListener('click', e => {
        const qa = e.target.closest('[data-action]');
        if (qa) { const { action, id } = qa.dataset; if (action === 'inc') changeQty(id, +1); if (action === 'dec') changeQty(id, -1); if (action === 'remove') removeItem(id); return; }
        if (e.target.closest('#applyVoucher,.btn-voucher')) { applyVoucher(); return; }
        const chip = e.target.closest('[data-voucher]'); if (chip) { const inp = $('voucherInput'); if (inp) inp.value = chip.dataset.voucher; applyVoucher(); return; }
        if (e.target.closest('[data-action="checkout"]')) { e.preventDefault(); const btn = e.target.closest('[data-action="checkout"]'); const orig = btn.innerHTML; btn.innerHTML = '… Đang xử lý'; btn.style.opacity = '.7'; setTimeout(() => { btn.innerHTML = orig; btn.style.opacity = ''; }, 2000); return; }
    });
    $('voucherInput')?.addEventListener('keydown', e => { if (e.key === 'Enter') { e.preventDefault(); applyVoucher(); } });

    document.addEventListener('DOMContentLoaded', () => { initSelectAll(); updateTotals(); });

}());


/* ============================================================
   03 · LOGIN PAGE  (guard: #loginPhone)
   ============================================================ */

if (document.getElementById('loginPhone') || document.getElementById('tabLogin')) (function initLogin() {

    const TABS = ['login', 'register', 'otp'];
    function switchTab(tab) { TABS.forEach(t => { const f = document.getElementById('form' + t[0].toUpperCase() + t.slice(1)), b = document.getElementById('tab' + t[0].toUpperCase() + t.slice(1)); if (f) f.style.display = t === tab ? '' : 'none'; if (b) b.classList.toggle('on', t === tab); }); }

    function doLogin() { const ph = document.getElementById('loginPhone')?.value.trim(), pw = document.getElementById('loginPass')?.value, err = document.getElementById('loginErr'); if (!ph || !pw) { err?.classList.add('show'); if (err) err.textContent = 'Vui lòng nhập đầy đủ thông tin.'; return; } err?.classList.remove('show'); goOTP(ph); }
    function doRegister() { const ph = document.getElementById('regPhone')?.value.trim(), pw = document.getElementById('regPass')?.value, cf = document.getElementById('regConfirm')?.value, agree = document.getElementById('agreeTerms')?.checked, err = document.getElementById('regErr'); if (!agree) { alert('Vui lòng đồng ý điều khoản.'); return; } if (pw !== cf) { err?.classList.add('show'); return; } err?.classList.remove('show'); goOTP(ph); }
    function goOTP(ph) { const sub = document.getElementById('otpSub'); if (sub) sub.innerHTML = `Mã 6 số đã gửi đến <b>${ph}</b>`; switchTab('otp'); startCD(); setTimeout(() => document.querySelector('.otp-digit')?.focus(), 100); }

    function handleOTP(inp) { const digits = [...document.querySelectorAll('.otp-digit')], idx = digits.indexOf(inp); if (inp.value.length > 1) inp.value = inp.value.slice(-1); inp.classList.toggle('filled', !!inp.value); if (inp.value && idx < digits.length - 1) digits[idx + 1].focus(); if (digits.every(d => d.value)) verifyOTP(); }
    function handleOTPKey(e, inp) { const digits = [...document.querySelectorAll('.otp-digit')], idx = digits.indexOf(inp); if (e.key === 'Backspace' && !inp.value && idx > 0) digits[idx - 1].focus(); }
    function verifyOTP() { const code = [...document.querySelectorAll('.otp-digit')].map(d => d.value).join(''); if (code.length < 6) return; window.location.href = 'profile.html'; }

    let _cdTimer = null;
    function startCD(secs = 60) { const rb = document.getElementById('resendBtn'), tw = document.getElementById('resendTimer'), ce = document.getElementById('countdown'); if (rb) { rb.disabled = true; rb.style.opacity = '.4'; } if (tw) tw.style.display = 'inline'; clearInterval(_cdTimer); _cdTimer = setInterval(() => { secs--; if (ce) ce.textContent = secs; if (secs <= 0) { clearInterval(_cdTimer); if (rb) { rb.disabled = false; rb.style.opacity = '1'; } if (tw) tw.style.display = 'none'; } }, 1000); }

    const SCOLORS = ['', '#dc2626', '#f59e0b', '#22c55e', '#15803d'], SLABELS = ['', 'Yếu', 'Trung bình', 'Mạnh', 'Rất mạnh'];
    function initStrength() { const inp = document.getElementById('regPass'); if (!inp) return; inp.addEventListener('input', function () { let s = 0; if (this.value.length >= 8) s++; if (/[A-Z]/.test(this.value)) s++; if (/[0-9]/.test(this.value)) s++; if (/[^A-Za-z0-9]/.test(this.value)) s++; document.querySelectorAll('.sb').forEach((b, i) => { b.style.background = i < s ? (SCOLORS[s] || '#e5e7eb') : '#e5e7eb'; }); const lbl = document.getElementById('strengthLabel'); if (lbl) { lbl.textContent = this.value ? SLABELS[s] : ''; lbl.style.color = SCOLORS[s] || 'var(--gray-muted)'; }; }); }

    document.addEventListener('click', e => {
        if (e.target.id === 'tabLogin') { switchTab('login'); return; } if (e.target.id === 'tabRegister') { switchTab('register'); return; }
        if (e.target.closest('#btnLogin')) { doLogin(); return; } if (e.target.closest('#btnRegister')) { doRegister(); return; }
        if (e.target.closest('#btnVerifyOTP')) { verifyOTP(); return; } if (e.target.closest('#resendBtn')) { startCD(); return; }
        const pt = e.target.closest('[data-pass-toggle]'); if (pt) { FTW.togglePass(pt.dataset.passToggle, pt); return; }
    });
    document.addEventListener('input', e => { if (e.target.classList.contains('otp-digit')) handleOTP(e.target); });
    document.addEventListener('keydown', e => { if (e.target.classList.contains('otp-digit')) handleOTPKey(e, e.target); });

    document.addEventListener('DOMContentLoaded', () => { switchTab('login'); initStrength(); });

}());


/* ============================================================
   04 · ORDERS PAGE  (guard: .order-card)
   ============================================================ */

if (document.querySelector('.order-card')) (function initOrders() {

    let _rating = 0;
    function renderStars(n) { document.querySelectorAll('.star-btn').forEach((s, i) => { s.textContent = i < n ? '★' : '☆'; s.style.color = i < n ? '#d4a017' : '#ccc'; }); }
    function initSearch() { const inp = document.getElementById('orderSearch'); if (!inp) return; inp.addEventListener('input', function () { const q = this.value.trim().toLowerCase(); document.querySelectorAll('.order-card').forEach(c => { const id = c.querySelector('.order-id')?.textContent.toLowerCase() || '', nm = c.querySelector('.order-thumb img')?.alt.toLowerCase() || ''; c.style.display = (!q || id.includes(q) || nm.includes(q)) ? '' : 'none'; }); }); }

    document.addEventListener('click', e => {
        const pill = e.target.closest('.order-filter-pill'); if (pill) { document.querySelectorAll('.order-filter-pill').forEach(p => p.classList.remove('on')); pill.classList.add('on'); const st = pill.dataset.status; document.querySelectorAll('.order-card').forEach(c => { c.style.display = (st === 'all' || c.dataset.status === st) ? '' : 'none'; }); return; }
        if (e.target.closest('[data-action="review"]')) { const m = document.getElementById('reviewModal'); if (m) { m.style.display = 'flex'; _rating = 0; renderStars(0); } return; }
        if (e.target.closest('[data-action="close-modal"]') || e.target.id === 'reviewModal') { const m = document.getElementById('reviewModal'); if (m) m.style.display = 'none'; return; }
        if (e.target.closest('[data-action="submit-review"]')) { if (_rating === 0) { FTW.showToast('Vui lòng chọn số sao!'); return; } document.getElementById('reviewModal').style.display = 'none'; FTW.showToast('Cảm ơn bạn đã đánh giá!'); return; }
        const sb = e.target.closest('.star-btn'); if (sb) { const stars = [...document.querySelectorAll('.star-btn')]; _rating = stars.indexOf(sb) + 1; renderStars(_rating); return; }
        if (e.target.closest('[data-action="cancel-order"]')) { if (confirm('Huỷ đơn hàng này?')) { const card = e.target.closest('.order-card'); if (card) { card.dataset.status = 'cancelled'; const b = card.querySelector('.badge-status'); if (b) { b.className = 'badge-status bs-cancelled'; b.innerHTML = '<span>Đã huỷ</span>'; } FTW.showToast('Đã huỷ đơn hàng'); } } return; }
        if (e.target.closest('[data-action="reorder"]')) { FTW.updateCartBadge(1); FTW.showToast('Đã thêm vào giỏ hàng'); return; }
    });
    document.addEventListener('mouseover', e => { const sb = e.target.closest('.star-btn'); if (!sb) return; const stars = [...document.querySelectorAll('.star-btn')], n = stars.indexOf(sb) + 1; stars.forEach((s, i) => { s.style.transform = i < n ? 'scale(1.2)' : ''; }); });
    document.addEventListener('mouseout', e => { if (!e.target.closest('.star-btn')) return; document.querySelectorAll('.star-btn').forEach(s => { s.style.transform = ''; }); });

    document.addEventListener('DOMContentLoaded', () => initSearch());

}());


/* ============================================================
   05 · PROFILE PAGE  (guard: .profile-avatar)
   ============================================================ */

if (document.querySelector('.profile-avatar')) (function initProfile() {

    function toggleEdit(sid) { const v = document.getElementById(sid + 'View'), e = document.getElementById(sid + 'Edit'), b = document.querySelector(`[data-edit="${sid}"]`); if (!v || !e) return; const editing = e.style.display !== 'none' && e.style.display !== ''; v.style.display = editing ? '' : 'none'; e.style.display = editing ? 'none' : ''; if (b) b.textContent = editing ? 'Chỉnh sửa' : 'Huỷ'; }
    function cancelEdit(sid) { const v = document.getElementById(sid + 'View'), e = document.getElementById(sid + 'Edit'), b = document.querySelector(`[data-edit="${sid}"]`); if (v) v.style.display = ''; if (e) e.style.display = 'none'; if (b) b.textContent = 'Chỉnh sửa'; }

    function initAvatar() { const inp = document.getElementById('avatarInput'), trig = document.querySelector('.profile-avatar-edit'); if (!inp) return; trig?.addEventListener('click', () => inp.click()); inp.addEventListener('change', function () { if (!this.files?.[0]) return; const r = new FileReader(); r.onload = e => { document.querySelectorAll('.profile-avatar,.acc-avatar').forEach(av => { av.innerHTML = `<img src="${e.target.result}" style="width:100%;height:100%;object-fit:cover;" alt="Avatar"/>`; }); FTW.showToast('Đã cập nhật ảnh đại diện'); }; r.readAsDataURL(this.files[0]); }); }

    document.addEventListener('click', e => {
        const eb = e.target.closest('[data-edit]'); if (eb) { toggleEdit(eb.dataset.edit); return; }
        const cb = e.target.closest('[data-cancel]'); if (cb) { cancelEdit(cb.dataset.cancel); return; }
        const sb = e.target.closest('[data-save]'); if (sb) { cancelEdit(sb.dataset.save); FTW.showToast(sb.dataset.msg || 'Đã lưu thay đổi'); return; }
        const ts = e.target.closest('.ts-track'); if (ts) { ts.classList.toggle('on'); const lbl = ts.closest('.toggle-switch')?.dataset.label || ''; FTW.showToast(`${lbl} ${ts.classList.contains('on') ? 'đã bật' : 'đã tắt'}`); return; }
        const pt = e.target.closest('[data-pass-toggle]'); if (pt) { FTW.togglePass(pt.dataset.passToggle, pt); return; }
        if (e.target.closest('[data-action="change-pw-toggle"]')) { const f = document.getElementById('changePwForm'); if (f) f.style.display = (!f.style.display || f.style.display === 'none') ? '' : 'none'; return; }
        if (e.target.closest('[data-action="save-pw"]')) { const np = document.getElementById('newPw')?.value, cp = document.getElementById('confirmPw')?.value, err = document.getElementById('pwErr'); if (!np || np !== cp) { if (err) { err.textContent = 'Mật khẩu không khớp.'; err.classList.add('show'); } return; } err?.classList.remove('show'); document.getElementById('changePwForm').style.display = 'none'; FTW.showToast('Đã đổi mật khẩu'); return; }
        if (e.target.closest('[data-action="add-address"]')) { const f = document.getElementById('addAddressForm'); if (f) { const h = !f.style.display || f.style.display === 'none'; f.style.display = h ? '' : 'none'; if (h) f.scrollIntoView({ behavior: 'smooth', block: 'nearest' }); } return; }
        if (e.target.closest('[data-action="save-address"]')) { const f = document.getElementById('addAddressForm'); if (f) f.style.display = 'none'; FTW.showToast('Đã thêm địa chỉ mới'); return; }
        const delB = e.target.closest('[data-action="delete-address"]'); if (delB) { if (confirm('Xoá địa chỉ này?')) { const c = delB.closest('.address-card'); if (c) { c.style.transition = 'opacity .3s'; c.style.opacity = '0'; setTimeout(() => c.remove(), 320); FTW.showToast('Đã xoá địa chỉ'); } } return; }
        const defB = e.target.closest('[data-action="set-default"]'); if (defB) { document.querySelectorAll('.address-card').forEach(c => { c.classList.remove('is-default'); c.querySelector('.address-default-tag')?.remove(); }); const c = defB.closest('.address-card'); if (c) { c.classList.add('is-default'); const t = document.createElement('span'); t.className = 'address-default-tag'; t.textContent = 'Mặc định'; c.prepend(t); } FTW.showToast('Đã đặt làm địa chỉ mặc định'); return; }
    });

    document.addEventListener('DOMContentLoaded', () => { initAvatar();['changePwForm', 'addAddressForm'].forEach(id => { const f = document.getElementById(id); if (f) f.style.display = 'none'; }); });

}());


/* ============================================================
   06 · PRODUCT DETAIL PAGE  (guard: #galleryMain)
   ============================================================ */

if (document.getElementById('galleryMain')) (function initProduct() {

    const state = { qty: 1, stockMax: 17, selectedSize: null, selectedColor: 'Đen', wished: false };
    const $ = id => document.getElementById(id);

    /* Gallery Swiper */
    let thumbsSwiper, mainSwiper;
    function initGallery() {
        if (typeof Swiper === 'undefined') return;
        thumbsSwiper = new Swiper('#galleryThumbs', { slidesPerView: 4, spaceBetween: 8, watchSlidesProgress: true, freeMode: true });
        mainSwiper = new Swiper('#galleryMain', { loop: false, spaceBetween: 0, speed: 400, thumbs: { swiper: thumbsSwiper }, navigation: { nextEl: '.pd-gallery__next', prevEl: '.pd-gallery__prev' }, pagination: { el: '.pd-gallery__dots', clickable: true } });
    }

    /* Color selector */
    function initColors() {
        document.querySelectorAll('.pd-color-btn').forEach(btn => {
            btn.addEventListener('click', function () { document.querySelectorAll('.pd-color-btn').forEach(b => { b.classList.remove('active'); b.setAttribute('aria-pressed', 'false'); }); this.classList.add('active'); this.setAttribute('aria-pressed', 'true'); state.selectedColor = this.dataset.color; const sc = $('selectedColor'); if (sc) sc.textContent = state.selectedColor; if (this.dataset.img && mainSwiper) { const s = document.querySelectorAll('#galleryMain .pd-img-wrap img'); if (s[0]) s[0].src = this.dataset.img; mainSwiper.slideTo(0, 300); } });
        });
    }

    /* Size selector */
    function initSizes() {
        document.querySelectorAll('.pd-size-btn:not(.out)').forEach(btn => {
            btn.addEventListener('click', function () { document.querySelectorAll('.pd-size-btn').forEach(b => { b.classList.remove('active'); b.setAttribute('aria-pressed', 'false'); }); this.classList.add('active'); this.setAttribute('aria-pressed', 'true'); state.selectedSize = this.dataset.size; const ss = $('selectedSize'); if (ss) ss.textContent = state.selectedSize; clearSizeErr(); });
        });
    }
    function clearSizeErr() { const e = $('sizeErr'); if (e) e.textContent = ''; }
    function requireSize() { if (state.selectedSize) return true; const e = $('sizeErr'); if (e) e.textContent = '⚠ Vui lòng chọn size trước khi thêm vào giỏ.'; document.getElementById('sizeGroup')?.scrollIntoView({ behavior: 'smooth', block: 'center' }); const sl = document.querySelector('.pd-size-list'); if (sl) { sl.style.animation = 'none'; sl.offsetHeight; sl.style.animation = 'shakeX .4s ease'; } return false; }

    /* Qty stepper */
    function setQty(n) { state.qty = Math.min(Math.max(1, n), state.stockMax); const qv = $('qtyVal'); if (qv) qv.textContent = state.qty; const qd = $('qtyDec'); if (qd) qd.disabled = state.qty <= 1; const qi = $('qtyInc'); if (qi) qi.disabled = state.qty >= state.stockMax; }
    $('qtyDec')?.addEventListener('click', () => setQty(state.qty - 1));
    $('qtyInc')?.addEventListener('click', () => setQty(state.qty + 1));

    /* Add to cart */
    function addToCart(source = 'main') {
        if (!requireSize()) return;
        const btn = source === 'sticky' ? document.querySelector('.pd-sticky-btn') : $('btnAddCart');
        if (!btn) return;
        const orig = btn.innerHTML;
        btn.classList.add('loading');
        btn.innerHTML = `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="animation:spin .7s linear infinite"><path d="M12 2a10 10 0 1 0 10 10"/></svg> Đang thêm…`;
        setTimeout(() => { btn.classList.remove('loading'); btn.innerHTML = orig; FTW.updateCartBadge(state.qty); FTW.showToast(`Đã thêm ${state.qty} sản phẩm vào giỏ hàng!`); }, 600);
    }

    /* Wishlist */
    function toggleWishlist() { state.wished = !state.wished; $('btnWishlist')?.classList.toggle('wished', state.wished); $('btnWishlist')?.setAttribute('aria-pressed', String(state.wished)); const icon = $('heartIcon'); if (icon) icon.style.fill = state.wished ? 'var(--price-red)' : 'none'; FTW.showToast(state.wished ? 'Đã thêm vào yêu thích ❤' : 'Đã xoá khỏi yêu thích'); }

    /* Size modal */
    function openSizeModal() { const m = $('sizeModal'); if (!m) return; m.hidden = false; document.body.style.overflow = 'hidden'; }
    function closeSizeModal() { const m = $('sizeModal'); if (!m) return; m.hidden = true; document.body.style.overflow = ''; }

    /* Tabs */
    function initTabs() {
        const btns = document.querySelectorAll('.pd-tab-btn'), panels = document.querySelectorAll('.pd-tab-panel');
        btns.forEach(btn => btn.addEventListener('click', function () { const t = this.dataset.tab; btns.forEach(b => { b.classList.remove('active'); b.setAttribute('aria-selected', 'false'); }); panels.forEach(p => { p.classList.remove('active'); p.hidden = true; }); this.classList.add('active'); this.setAttribute('aria-selected', 'true'); const p = document.getElementById('tab' + t[0].toUpperCase() + t.slice(1)); if (p) { p.classList.add('active'); p.hidden = false; } }));
    }

    /* Sticky bar */
    function initStickyBar() { const sb = $('stickyBar'), actEl = document.querySelector('.pd-actions'); if (!sb || !actEl) return; new IntersectionObserver(([e]) => { const show = !e.isIntersecting; sb.classList.toggle('visible', show); sb.setAttribute('aria-hidden', String(!show)); }, { threshold: 0 }).observe(actEl); }

    /* Image zoom */
    function initZoom() {
        let ov = document.querySelector('.pd-zoom-overlay');
        if (!ov) { ov = document.createElement('div'); ov.className = 'pd-zoom-overlay'; ov.hidden = true; ov.innerHTML = '<button class="pd-zoom-close" aria-label="Đóng">×</button><img src="" alt="Phóng to"/>'; document.body.appendChild(ov); }
        const zImg = ov.querySelector('img');
        document.addEventListener('click', e => { const iw = e.target.closest('.pd-img-wrap'); if (iw) { const src = iw.querySelector('img')?.src; if (!src) return; zImg.src = src; ov.hidden = false; document.body.style.overflow = 'hidden'; return; } if (e.target === ov || e.target.closest('.pd-zoom-close')) { ov.hidden = true; document.body.style.overflow = ''; } });
        document.addEventListener('keydown', e => { if (e.key === 'Escape' && !ov.hidden) { ov.hidden = true; document.body.style.overflow = ''; } });
    }

    /* Share */
    function initShare() {
        document.querySelectorAll('.pd-share__btn').forEach(btn => { btn.addEventListener('click', e => { e.preventDefault(); const lbl = btn.getAttribute('aria-label') || ''; if (lbl.includes('Copy') || lbl.includes('link')) { navigator.clipboard?.writeText(location.href).then(() => FTW.showToast('Đã sao chép link!')); } else if (lbl.includes('Facebook')) { window.open(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(location.href)}`, '_blank', 'width=600,height=400'); } }); });
    }

    /* Event delegation for product page */
    document.addEventListener('click', e => {
        if (e.target.closest('#btnAddCart')) { addToCart('main'); return; }
        if (e.target.closest('.pd-sticky-btn')) { addToCart('sticky'); return; }
        if (e.target.closest('#btnBuyNow')) { if (!requireSize()) return; window.location.href = 'cart.html'; return; }
        if (e.target.closest('#btnWishlist')) { toggleWishlist(); return; }
        if (e.target.closest('#sizeGuideBtn')) { openSizeModal(); return; }
        if (e.target.closest('[data-action="close-modal"]') || e.target.id === 'sizeModal') { closeSizeModal(); return; }
    });

    function checkHash() { if (location.hash === '#reviews') { const rb = document.querySelector('[data-tab="reviews"]'); if (rb) { rb.click(); setTimeout(() => rb.scrollIntoView({ behavior: 'smooth', block: 'start' }), 100); } } }

    document.addEventListener('DOMContentLoaded', () => { initGallery(); initColors(); initSizes(); setQty(1); initTabs(); initStickyBar(); initZoom(); initShare(); checkHash(); FTW.observeCards(); });

}());


/* ============================================================
   07 · CHECKOUT PAGE  (guard: #checkoutMain)
   ============================================================ */

if (document.getElementById('checkoutMain')) (function initCheckout() {

    const $ = id => document.getElementById(id);
    const ITEMS = [
        { name: 'SHEEPSMAN FACE HOODIE', variant: 'Đen / L', qty: 1, price: 650000, img: 'https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-lzaxggq7qeud1b' },
        { name: 'LED SIGN /teddy bear/ NEW TEE™', variant: 'Trắng / M', qty: 2, price: 390000, img: 'https://down-vn.img.susercontent.com/file/vn-11134207-7ras8-m1pkq9zy9s3l4b' },
    ];
    const VOUCHERS = { '5TW10': { pct: 10 }, 'SALE20': { pct: 20 }, 'FREESHIP': { pct: 0, freeShip: true } };
    const SHIP_METHODS = { standard: { label: 'Giao hàng tiêu chuẩn', days: '2–4 ngày', cost: 30000 }, express: { label: 'Giao hàng nhanh', days: '1–2 ngày', cost: 50000 } };
    const FREE_SHIP = 500000;

    let state = { step: 1, shipMethod: 'standard', payMethod: 'cod', discountPct: 0, freeShip: false, voucherCode: '' };

    /* ── Render order summary ──────────────────────────────── */
    function renderSummary() {
        const list = $('coItemList'); if (!list) return;
        list.innerHTML = ITEMS.map(it => `
      <div class="co-item">
        <div class="co-item__img"><img src="${it.img}" alt="${it.name}" loading="lazy"/><span class="co-item__qty">${it.qty}</span></div>
        <div class="co-item__info"><p class="co-item__name">${it.name}</p><p class="co-item__var">${it.variant}</p></div>
        <span class="co-item__price">${FTW.fmt(it.price * it.qty)}</span>
      </div>`).join('');
        updateSummaryTotals();
    }

    function updateSummaryTotals() {
        const sub = ITEMS.reduce((a, it) => a + it.price * it.qty, 0);
        const ship = state.freeShip || sub >= FREE_SHIP ? 0 : SHIP_METHODS[state.shipMethod].cost;
        const disc = Math.round(sub * state.discountPct / 100);
        const total = sub + ship - disc;

        setText('coSubtotal', FTW.fmt(sub));
        setText('coShip', ship === 0 ? '<span style="color:#15803d;font-weight:700;">Miễn phí</span>' : FTW.fmt(ship), true);
        const dr = $('coDiscountRow'); if (dr) dr.style.display = disc > 0 ? 'flex' : 'none';
        setText('coDiscount', disc > 0 ? '−' + FTW.fmt(disc) : '');
        setText('coTotal', FTW.fmt(total));

        // Free ship progress in summary
        const pctEl = $('coShipProgress');
        if (pctEl) { pctEl.style.display = ship > 0 ? '' : 'none'; const pct = Math.min(100, (sub / FREE_SHIP) * 100); const fill = pctEl.querySelector('.co-ship-fill'); if (fill) fill.style.width = pct + '%'; const need = pctEl.querySelector('.co-ship-need'); if (need) need.textContent = FTW.fmt(FREE_SHIP - sub); }
    }

    function setText(id, val, html = false) { const el = $(id); if (!el) return; if (html) el.innerHTML = val; else el.textContent = val; }

    /* ── Shipping method selection ─────────────────────────── */
    function initShipMethods() {
        document.querySelectorAll('.co-ship-option').forEach(opt => {
            opt.addEventListener('click', function () {
                document.querySelectorAll('.co-ship-option').forEach(o => o.classList.remove('active'));
                this.classList.add('active');
                state.shipMethod = this.dataset.ship;
                updateSummaryTotals();
            });
        });
    }

    /* ── Payment method selection ──────────────────────────── */
    function initPayMethods() {
        document.querySelectorAll('.co-pay-option').forEach(opt => {
            opt.addEventListener('click', function () {
                document.querySelectorAll('.co-pay-option').forEach(o => o.classList.remove('active'));
                this.classList.add('active');
                state.payMethod = this.dataset.pay;
                // Show/hide bank details panel
                const bankPanel = $('bankPanel');
                if (bankPanel) bankPanel.style.display = state.payMethod === 'bank' ? '' : 'none';
            });
        });
    }

    /* ── Voucher ───────────────────────────────────────────── */
    function applyVoucher() {
        const inp = $('coVoucherInput'), msg = $('coVoucherMsg');
        const code = inp?.value.trim().toUpperCase() || '';
        const v = VOUCHERS[code];
        if (v !== undefined) {
            state.discountPct = v.pct || 0;
            state.freeShip = !!v.freeShip;
            state.voucherCode = code;
            if (msg) { msg.style.display = 'block'; msg.style.color = '#15803d'; msg.textContent = `✓ Áp dụng thành công — ${v.freeShip ? 'miễn phí vận chuyển' : 'giảm ' + v.pct + '%'}`; }
        } else {
            state.discountPct = 0; state.freeShip = false; state.voucherCode = '';
            if (msg) { msg.style.display = 'block'; msg.style.color = 'var(--price-red)'; msg.textContent = '✗ Mã không hợp lệ hoặc đã hết hạn'; }
        }
        updateSummaryTotals();
    }

    /* ── Form validation ───────────────────────────────────── */
    function validateForm() {
        const required = ['coName', 'coPhone', 'coAddress'];
        let valid = true;
        required.forEach(id => {
            const el = $(id); if (!el) return;
            const empty = !el.value.trim();
            el.classList.toggle('co-input-err', empty);
            if (empty) valid = false;
        });
        if (!valid) {
            FTW.showToast('Vui lòng điền đầy đủ thông tin giao hàng!');
            document.querySelector('.co-input-err')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        return valid;
    }

    /* ── Place order ───────────────────────────────────────── */
    function placeOrder(btn) {
        if (!validateForm()) return;
        const orig = btn.innerHTML;
        btn.innerHTML = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="animation:spin .7s linear infinite"><path d="M12 2a10 10 0 1 0 10 10"/></svg> Đang xử lý…`;
        btn.disabled = true;

        setTimeout(() => {
            btn.innerHTML = orig;
            btn.disabled = false;
            showSuccessModal();
        }, 1400);
    }

    /* ── Success modal ─────────────────────────────────────── */
    function showSuccessModal() {
        const overlay = $('successOverlay');
        if (!overlay) return;
        overlay.hidden = false;
        document.body.style.overflow = 'hidden';
        // Generate fake order ID
        const orderId = '#5TW-' + Date.now().toString().slice(-6);
        const el = overlay.querySelector('.co-success-order-id');
        if (el) el.textContent = orderId;
    }

    /* ── Summary toggle (mobile collapse) ─────────────────── */
    function initSummaryToggle() {
        const toggle = $('coSummaryToggle');
        const body = $('coSummaryBody');
        if (!toggle || !body) return;
        toggle.addEventListener('click', () => {
            const open = body.style.display !== 'none';
            body.style.display = open ? 'none' : '';
            toggle.querySelector('.co-chevron')?.classList.toggle('open', !open);
        });
    }

    /* ── Event delegation ──────────────────────────────────── */
    document.addEventListener('click', e => {
        if (e.target.closest('#coApplyVoucher,.co-voucher-btn')) { applyVoucher(); return; }
        const vc = e.target.closest('[data-voucher]'); if (vc) { const inp = $('coVoucherInput'); if (inp) inp.value = vc.dataset.voucher; applyVoucher(); return; }
        if (e.target.closest('#btnPlaceOrder')) { e.preventDefault(); placeOrder(e.target.closest('#btnPlaceOrder')); return; }
        if (e.target.closest('#btnPlaceOrderMobile')) { e.preventDefault(); placeOrder(e.target.closest('#btnPlaceOrderMobile')); return; }
        if (e.target.closest('#btnSuccessOrders')) { window.location.href = 'orders.html'; return; }
        if (e.target.closest('#btnSuccessHome')) { window.location.href = 'index.html'; return; }
        // Copy to clipboard buttons (bank transfer)
        const copyBtn = e.target.closest('.co-copy-btn[data-copy]');
        if (copyBtn) {
            const text = copyBtn.dataset.copy;
            navigator.clipboard?.writeText(text).then(() => {
                const orig = copyBtn.innerHTML;
                copyBtn.innerHTML = '✓ Đã sao chép';
                copyBtn.style.background = '#22c55e'; copyBtn.style.color = '#fff'; copyBtn.style.borderColor = '#22c55e';
                setTimeout(() => { copyBtn.innerHTML = orig; copyBtn.style.cssText = ''; }, 1600);
            });
            return;
        }
        const pt = e.target.closest('[data-pass-toggle]'); if (pt) { FTW.togglePass(pt.dataset.passToggle, pt); return; }
    });

    $('coVoucherInput')?.addEventListener('keydown', e => { if (e.key === 'Enter') { e.preventDefault(); applyVoucher(); } });

    // Clear error state on input
    document.querySelectorAll('.co-input').forEach(inp => {
        inp.addEventListener('input', function () { this.classList.remove('co-input-err'); });
    });

    document.addEventListener('DOMContentLoaded', () => {
        renderSummary();
        initShipMethods();
        initPayMethods();
        initSummaryToggle();
        // Init first payment option as active
        document.querySelector('.co-pay-option')?.classList.add('active');
        document.querySelector('.co-ship-option')?.classList.add('active');
        const bankPanel = $('bankPanel'); if (bankPanel) bankPanel.style.display = 'none';
    });

}());


/* ============================================================
   08 · NEWS PAGE  (guard: #nwCards)
   ============================================================ */

if (document.getElementById('nwCards')) (function initNews() {

    'use strict';

    /* ── DOM refs ──────────────────────────────────────────── */
    const $ = id => document.getElementById(id);
    const cards = () => [...document.querySelectorAll('#nwCards .nw-card')];
    const featured = document.querySelector('#nwFeatured .nw-featured');
    const cardsWrap = $('nwCards');
    const emptyState = $('nwEmpty');
    const resultEl = $('nwResultCount');
    const sortSel = $('nwSort');
    const loadMoreWrap = $('nwLoadMore');
    const progressFill = $('nwProgressFill');
    const progressMeta = $('nwProgressMeta');
    const loadBtn = $('nwLoadBtn');
    const searchInp = $('newsSearch');
    const backTopBtn = $('nwBackTop');
    const nlEmail = $('nlEmail');
    const nlSubmit = $('nlSubmit');
    const nlMsg = $('nlMsg');

    /* ── State ─────────────────────────────────────────────── */
    const INITIAL_VISIBLE = 9;
    const BATCH = 3;
    let state = {
        cat: 'all',
        sort: 'newest',
        query: '',
        loaded: INITIAL_VISIBLE,
    };

    /* ── Filter + sort + search (single pass) ──────────────── */
    function applyAll() {
        const all = cards();
        const q = state.query.toLowerCase().trim();

        // 1. Determine which cards match filters
        let matched = [];
        all.forEach(card => {
            const cat = card.dataset.cat || '';
            const title = card.querySelector('.nw-card__title')?.textContent.toLowerCase() || '';
            const excerpt = card.querySelector('.nw-card__excerpt')?.textContent.toLowerCase() || '';

            const catOk = state.cat === 'all' || cat === state.cat;
            const queryOk = !q || title.includes(q) || excerpt.includes(q) || cat.includes(q);

            if (catOk && queryOk) matched.push(card);
        });

        // 2. Sort matched list
        matched = sortList(matched);

        // 3. Reorder DOM (append sorted cards; unmatched go to end hidden)
        const unmatched = all.filter(c => !matched.includes(c));
        [...matched, ...unmatched].forEach(c => cardsWrap.appendChild(c));

        // 4. Reset loaded counter
        state.loaded = INITIAL_VISIBLE;

        // 5. Show/hide
        matched.forEach((card, i) => {
            const visible = i < state.loaded;
            card.style.display = visible ? '' : 'none';
            card.classList.toggle('nw-hidden', !visible);
        });
        unmatched.forEach(c => { c.style.display = 'none'; c.classList.add('nw-hidden'); });

        // 6. Featured visibility — hide if no lookbook match
        if (featured) {
            const featCat = featured.dataset.cat || '';
            const featTitle = featured.querySelector('.nw-featured__title')?.textContent.toLowerCase() || '';
            const featOk = (state.cat === 'all' || featCat === state.cat) &&
                (!q || featTitle.includes(q));
            $('nwFeatured').style.display = featOk ? '' : 'none';
        }

        // 7. Empty state
        const totalMatch = matched.length;
        emptyState.hidden = totalMatch > 0;
        cardsWrap.style.display = totalMatch > 0 ? '' : 'none';

        // 8. Update UI
        updateCount(totalMatch);
        updateProgress(Math.min(state.loaded, totalMatch), totalMatch);
        updateLoadBtn(Math.min(state.loaded, totalMatch), totalMatch);

        // 9. Animate newly visible cards
        animateCards();
    }

    /* ── Sort helper ───────────────────────────────────────── */
    function sortList(list) {
        return [...list].sort((a, b) => {
            if (state.sort === 'newest') {
                return new Date(b.dataset.date) - new Date(a.dataset.date);
            }
            if (state.sort === 'oldest') {
                return new Date(a.dataset.date) - new Date(b.dataset.date);
            }
            if (state.sort === 'popular') {
                return parseInt(b.dataset.views || 0) - parseInt(a.dataset.views || 0);
            }
            return 0;
        });
    }

    /* ── Load more ─────────────────────────────────────────── */
    function loadMore() {
        const all = cards();
        const q = state.query.toLowerCase().trim();
        const matched = sortList(all.filter(c => {
            const catOk = state.cat === 'all' || c.dataset.cat === state.cat;
            const title = c.querySelector('.nw-card__title')?.textContent.toLowerCase() || '';
            const excerpt = c.querySelector('.nw-card__excerpt')?.textContent.toLowerCase() || '';
            const queryOk = !q || title.includes(q) || excerpt.includes(q);
            return catOk && queryOk;
        }));

        const prev = state.loaded;
        state.loaded = Math.min(state.loaded + BATCH, matched.length);

        matched.slice(prev, state.loaded).forEach(card => {
            card.style.display = '';
            card.classList.remove('nw-hidden');
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            card.style.transition = 'opacity .4s ease, transform .4s ease';
            // Trigger repaint then animate
            requestAnimationFrame(() => requestAnimationFrame(() => {
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }));
        });

        updateProgress(state.loaded, matched.length);
        updateLoadBtn(state.loaded, matched.length);
    }

    /* ── Update count label ────────────────────────────────── */
    function updateCount(n) {
        if (resultEl) resultEl.textContent = n + ' bài viết';
    }

    /* ── Progress bar + meta ───────────────────────────────── */
    function updateProgress(shown, total) {
        if (progressFill) progressFill.style.width = total ? (shown / total * 100) + '%' : '0%';
        if (progressMeta) progressMeta.textContent = `Đang xem ${shown} / ${total} bài viết`;
    }

    /* ── Load more button visibility ──────────────────────── */
    function updateLoadBtn(shown, total) {
        if (!loadMoreWrap) return;
        const rem = total - shown;
        loadMoreWrap.style.display = rem <= 0 && total === 0 ? 'none' : '';
        if (loadBtn) {
            loadBtn.style.display = rem <= 0 ? 'none' : '';
            if (rem > 0) {
                loadBtn.innerHTML = `<svg width="15" height="15" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <polyline points="1 4 1 10 7 10"/><path d="M3.51 15a9 9 0 1 0 .49-3.51"/>
        </svg> Tải thêm ${Math.min(rem, BATCH)} bài viết`;
            }
        }
    }

    /* ── Card entrance animation ───────────────────────────── */
    function animateCards() {
        const visible = cards().filter(c => c.style.display !== 'none');
        visible.forEach((card, i) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(16px)';
            card.style.transition = `opacity .38s ease ${i * 0.055}s, transform .38s ease ${i * 0.055}s`;
            requestAnimationFrame(() => requestAnimationFrame(() => {
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }));
        });
    }

    /* ── Category filter ───────────────────────────────────── */
    function setCat(cat) {
        state.cat = cat;

        // Update .nw-cat buttons
        document.querySelectorAll('.nw-cat').forEach(btn => {
            const isActive = btn.dataset.cat === cat;
            btn.classList.toggle('active', isActive);
            btn.setAttribute('aria-selected', String(isActive));
        });

        // Update sidebar .nw-tag buttons
        document.querySelectorAll('.nw-tag').forEach(tag => {
            tag.classList.toggle('active', tag.dataset.cat === cat);
        });

        applyAll();
    }

    /* ── Search (debounced) ────────────────────────────────── */
    let _searchTimer;
    function onSearch(q) {
        clearTimeout(_searchTimer);
        _searchTimer = setTimeout(() => {
            state.query = q;
            applyAll();
        }, 280);
    }

    /* ── Newsletter ────────────────────────────────────────── */
    function submitNewsletter() {
        if (!nlEmail || !nlMsg) return;
        const email = nlEmail.value.trim();
        const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRe.test(email)) {
            nlMsg.style.display = 'block';
            nlMsg.className = 'nw-nl-msg error';
            nlMsg.textContent = '✗ Vui lòng nhập email hợp lệ.';
            nlEmail.focus();
            return;
        }

        // Success feedback
        nlMsg.style.display = 'block';
        nlMsg.className = 'nw-nl-msg success';
        nlMsg.textContent = '✓ Đã đăng ký thành công!';
        nlEmail.value = '';
        if (nlSubmit) { nlSubmit.innerHTML = '✓'; nlSubmit.style.background = 'rgba(74,222,128,.25)'; }

        setTimeout(() => {
            nlMsg.style.display = 'none';
            if (nlSubmit) {
                nlSubmit.innerHTML = `<svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>`;
                nlSubmit.style.background = '';
            }
        }, 3200);
    }

    /* ── Back to top ───────────────────────────────────────── */
    function initBackTop() {
        if (!backTopBtn) return;
        window.addEventListener('scroll', () => {
            backTopBtn.classList.toggle('show', window.scrollY > 500);
        }, { passive: true });
        backTopBtn.addEventListener('click', () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }

    /* ── Sticky filter bar highlight on scroll ─────────────── */
    function initFilterBarShadow() {
        const bar = $('nwFilterBar');
        if (!bar) return;
        const obs = new IntersectionObserver(
            ([entry]) => bar.classList.toggle('scrolled', !entry.isIntersecting),
            { threshold: 1, rootMargin: `-${parseInt(getComputedStyle(document.documentElement).getPropertyValue('--nav-h') || '60')}px 0px 0px 0px` }
        );
        // Sentinel above filter bar
        const sentinel = document.createElement('div');
        sentinel.style.cssText = 'height:1px;margin-top:-1px;pointer-events:none;';
        bar.before(sentinel);
        obs.observe(sentinel);
    }

    /* ── Smooth filter-bar scroll on mobile (centre active tab) */
    function scrollActiveCatIntoView(btn) {
        if (!btn) return;
        const bar = $('nwCats');
        if (!bar) return;
        const btnLeft = btn.offsetLeft;
        const btnWidth = btn.offsetWidth;
        const barWidth = bar.offsetWidth;
        bar.scrollTo({ left: btnLeft - barWidth / 2 + btnWidth / 2, behavior: 'smooth' });
    }

    /* ── Event delegation ──────────────────────────────────── */
    document.addEventListener('click', e => {
        // Category filter buttons
        const catBtn = e.target.closest('.nw-cat[data-cat]');
        if (catBtn) {
            setCat(catBtn.dataset.cat);
            scrollActiveCatIntoView(catBtn);
            return;
        }

        // Sidebar tag cloud
        const tagBtn = e.target.closest('.nw-tag[data-cat]');
        if (tagBtn) {
            setCat(tagBtn.dataset.cat);
            // Scroll to filter bar on mobile
            if (window.innerWidth < 992) {
                $('nwFilterBar')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
            return;
        }

        // Load more
        if (e.target.closest('#nwLoadBtn')) {
            loadMore();
            return;
        }

        // Reset (empty state button)
        if (e.target.closest('#nwReset')) {
            state.query = '';
            if (searchInp) searchInp.value = '';
            setCat('all');
            return;
        }

        // Newsletter submit
        if (e.target.closest('#nlSubmit')) {
            submitNewsletter();
            return;
        }
    });

    // Search input
    searchInp?.addEventListener('input', e => onSearch(e.target.value));
    searchInp?.addEventListener('keydown', e => { if (e.key === 'Escape') { state.query = ''; searchInp.value = ''; applyAll(); } });

    // Newsletter Enter key
    nlEmail?.addEventListener('keydown', e => { if (e.key === 'Enter') { e.preventDefault(); submitNewsletter(); } });

    // Sort
    sortSel?.addEventListener('change', function () {
        state.sort = this.value;
        applyAll();
    });

    /* ── Init ──────────────────────────────────────────────── */
    document.addEventListener('DOMContentLoaded', () => {
        applyAll();
        initBackTop();
        initFilterBarShadow();
    });

}());