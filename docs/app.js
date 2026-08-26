(() => {
  "use strict";

  const catalog = window.OPUS_CATALOG || { recipes: [], trophies: [], entries: [] };
  const recipeGrid = document.querySelector("#recipe-grid");
  const trophyGrid = document.querySelector("#trophy-grid");
  const entryGrid = document.querySelector("#entry-grid");
  const entrySearch = document.querySelector("#entry-search");
  const entryFilter = document.querySelector("#entry-filter");
  const recipeSearch = document.querySelector("#recipe-search");
  const siteSearch = document.querySelector("#site-search");
  const noResults = document.querySelector("#no-results");
  let recipeFilter = "all";
  let globalTerm = "";

  const escapeHtml = value => String(value).replace(/[&<>'"]/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;"
  })[character]);
  const normalized = value => String(value || "").toLocaleLowerCase().normalize("NFKD");

  function icon(item) {
    if (item.image) return `<span class="item-icon"><img src="${escapeHtml(item.image)}" alt="" loading="lazy"></span>`;
    const initials = item.name.split(/\s+/).slice(0, 2).map(word => word[0]).join("");
    return `<span class="item-icon"><span class="item-fallback">${escapeHtml(initials)}</span></span>`;
  }

  function shapedBody(recipe) {
    const rows = [...recipe.pattern];
    while (rows.length < 3) rows.push("");
    const cells = rows.slice(0, 3).flatMap(row => row.padEnd(3, " ").slice(0, 3).split(""));
    const used = [...new Set(cells.filter(symbol => symbol.trim()))];
    return `<div class="craft-grid" aria-label="3 by 3 crafting pattern">${cells.map(symbol => {
      const entry = recipe.key[symbol];
      return `<span class="craft-cell" title="${entry ? escapeHtml(entry.name) : "Empty"}">${symbol.trim() ? escapeHtml(symbol) : ""}</span>`;
    }).join("")}</div><ul class="recipe-legend">${used.map(symbol => `<li><b>${escapeHtml(symbol)}</b><span>${escapeHtml(recipe.key[symbol].name)}</span></li>`).join("")}</ul>`;
  }

  function ingredientBody(recipe) {
    const ingredients = recipe.ingredients || [];
    return `<div class="ingredient-list">${ingredients.map(entry => `<span class="ingredient-pill">${escapeHtml(entry.name)}</span>`).join("")}</div>${recipe.catalyst ? '<p class="recipe-id">Catalyst is returned after forging</p>' : ""}`;
  }

  function recipeCard(recipe) {
    return `<article class="recipe-card" data-search="${escapeHtml(normalized(`${recipe.name} ${recipe.id} ${recipe.type}`))}">
      <div class="recipe-head">${icon(recipe)}<div><h3>${escapeHtml(recipe.name)}${recipe.count > 1 ? ` ×${recipe.count}` : ""}</h3><span class="recipe-type">${escapeHtml(recipe.type)}</span></div></div>
      ${recipe.pattern ? shapedBody(recipe) : ingredientBody(recipe)}
      <div class="recipe-id">opusvsexe:${escapeHtml(recipe.id)}</div>
    </article>`;
  }

  function recipeMatches(recipe) {
    const local = normalized(recipeSearch.value);
    const haystack = normalized(`${recipe.name} ${recipe.id} ${recipe.type} ${(recipe.ingredients || []).map(item => item.name).join(" ")} ${Object.values(recipe.key || {}).map(item => item.name).join(" ")}`);
    return (recipeFilter === "all" || recipe.type === recipeFilter) && (!local || haystack.includes(local)) && (!globalTerm || haystack.includes(globalTerm));
  }

  function renderRecipes() {
    const recipes = catalog.recipes.filter(recipeMatches);
    recipeGrid.innerHTML = recipes.length ? recipes.map(recipeCard).join("") : '<p class="empty-catalog">No recipes match these filters.</p>';
    return recipes.length;
  }

  function renderTrophies() {
    trophyGrid.innerHTML = catalog.trophies.map(trophy => `<article class="trophy-card" data-search="${escapeHtml(normalized(`${trophy.name} ${trophy.id} ${trophy.description}`))}">
      <img src="${escapeHtml(trophy.image)}" alt="${escapeHtml(trophy.name)}" loading="lazy">
      <h3>${escapeHtml(trophy.name)}</h3><p>${escapeHtml(trophy.description)}</p>
    </article>`).join("");
    document.querySelector("#recipe-count").textContent = catalog.recipes.length;
    document.querySelector("#trophy-count").textContent = catalog.trophies.length;
    document.querySelector("#archive-total").textContent = `${catalog.trophies.length} / ${catalog.trophies.length}`;
  }

  function entryMatches(entry) {
    const local = normalized(entrySearch.value);
    const haystack = normalized(`${entry.name} ${entry.id} ${entry.kind} ${entry.category}`);
    return (entryFilter.value === "all" || entry.category === entryFilter.value) && (!local || haystack.includes(local)) && (!globalTerm || haystack.includes(globalTerm));
  }

  function renderEntries() {
    const entries = catalog.entries.filter(entryMatches);
    entryGrid.innerHTML = entries.length ? entries.map(entry => `<article class="entry-card">
      ${icon(entry)}<h3>${escapeHtml(entry.name)}</h3><span>${escapeHtml(entry.kind)} · opusvsexe:${escapeHtml(entry.id)}</span>
    </article>`).join("") : '<p class="empty-catalog">No items or blocks match these filters.</p>';
    document.querySelector("#entry-summary").textContent = `Showing ${entries.length} of ${catalog.entries.length} localized entries`;
    return entries.length;
  }

  [...new Set(catalog.entries.map(entry => entry.category))].sort().forEach(category => {
    const option = document.createElement("option"); option.value = category; option.textContent = category; entryFilter.append(option);
  });
  entrySearch.addEventListener("input", renderEntries);
  entryFilter.addEventListener("change", renderEntries);

  document.querySelector("#recipe-filters").addEventListener("click", event => {
    const button = event.target.closest("button[data-filter]");
    if (!button) return;
    recipeFilter = button.dataset.filter;
    document.querySelectorAll("#recipe-filters button").forEach(item => item.classList.toggle("active", item === button));
    renderRecipes();
  });
  recipeSearch.addEventListener("input", renderRecipes);

  function applySiteSearch() {
    globalTerm = normalized(siteSearch.value.trim());
    const recipeMatchesCount = renderRecipes();
    const entryMatchesCount = renderEntries();
    let trophyMatchesCount = 0;
    document.querySelectorAll(".trophy-card").forEach(card => {
      const match = !globalTerm || card.dataset.search.includes(globalTerm);
      card.classList.toggle("search-hidden", !match);
      if (match) trophyMatchesCount++;
    });
    let visible = 0;
    document.querySelectorAll(".searchable").forEach(section => {
      const ownMatch = !globalTerm || normalized(`${section.dataset.search || ""} ${section.textContent}`).includes(globalTerm);
      const catalogMatch = section.id === "crafting" ? recipeMatchesCount > 0 : section.id === "compendium" ? entryMatchesCount > 0 : section.id === "trophies" ? trophyMatchesCount > 0 : false;
      const show = !globalTerm || ownMatch || catalogMatch;
      section.classList.toggle("search-hidden", !show);
      if (show) visible++;
    });
    noResults.hidden = visible > 0;
  }
  siteSearch.addEventListener("input", applySiteSearch);
  document.addEventListener("keydown", event => {
    if (event.key === "/" && !/INPUT|TEXTAREA/.test(document.activeElement.tagName)) {
      event.preventDefault(); siteSearch.focus();
    }
    if (event.key === "Escape") { siteSearch.value = ""; applySiteSearch(); siteSearch.blur(); }
  });

  const navToggle = document.querySelector("#nav-toggle");
  const sidebar = document.querySelector("#site-nav");
  navToggle.addEventListener("click", () => {
    const open = sidebar.classList.toggle("open");
    navToggle.setAttribute("aria-expanded", String(open));
  });
  sidebar.querySelectorAll("nav a").forEach(link => link.addEventListener("click", () => {
    sidebar.classList.remove("open"); navToggle.setAttribute("aria-expanded", "false");
  }));

  const navigationLinks = [...sidebar.querySelectorAll("nav a")];
  const observer = new IntersectionObserver(entries => {
    const visibleEntry = entries.filter(entry => entry.isIntersecting).sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];
    if (!visibleEntry) return;
    navigationLinks.forEach(link => link.classList.toggle("active", link.hash === `#${visibleEntry.target.id}`));
  }, { rootMargin: "-15% 0px -68% 0px", threshold: [0, .1, .4] });
  document.querySelectorAll("main section[id]").forEach(section => observer.observe(section));

  const readingProgress = document.querySelector("#reading-progress");
  function updateProgress() {
    const scrollable = document.documentElement.scrollHeight - window.innerHeight;
    readingProgress.style.width = `${scrollable > 0 ? Math.min(100, window.scrollY / scrollable * 100) : 0}%`;
  }
  document.addEventListener("scroll", updateProgress, { passive: true });

  document.querySelectorAll("code").forEach(code => {
    if (!code.textContent.startsWith("/")) return;
    code.title = "Click to copy command";
    code.tabIndex = 0;
    const copy = async () => {
      try { await navigator.clipboard.writeText(code.textContent); code.dataset.original = code.textContent; code.textContent = "Copied"; setTimeout(() => { code.textContent = code.dataset.original; }, 900); } catch (_) { /* file:// may deny clipboard */ }
    };
    code.addEventListener("click", copy);
    code.addEventListener("keydown", event => { if (event.key === "Enter" || event.key === " ") copy(); });
  });

  renderTrophies();
  document.querySelector("#entry-count").textContent = catalog.entries.length;
  renderEntries();
  renderRecipes();
  updateProgress();
})();
