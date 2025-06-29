const API_BASE = "http://localhost:8080";


const DEFAULT_ID = 0;

// 🔄 Récupération des scores
async function fetchScores() {
  console.log("📊 [SCORES] Récupération en cours...");
  try {
    const res = await fetch(`${API_BASE}/scores`);
    const data = await res.json();
    console.log("✅ [SCORES] Données reçues :", data);

    const list = document.getElementById("scoreList");
    list.innerHTML = "";
    data.sort((a, b) => b.score - a.score).forEach(player => {
      const li = document.createElement("li");
      li.innerHTML = `Joueur ${player.id} <span>${player.score} pts</span>`;
      list.appendChild(li);
    });
  } catch (err) {
    console.error("❌ [SCORES] Erreur lors de la récupération :", err);
  }
}

// 🔁 Réinitialisation du jeu
async function handleReset() {
  console.log("🔄 [RESET] Envoi de la commande de réinitialisation...");
  try {
    const res = await fetch(`${API_BASE}/reset`, { method: "POST" });
    const text = await res.text();
    console.log(`✅ [RESET] Réponse : "${text}"`);
    await fetchScores();
  } catch (err) {
    console.error("❌ [RESET] Erreur lors de la réinitialisation :", err);
    alert("Erreur pendant la réinitialisation");
  }
}

// ✅ Validation d'une bonne réponse
async function handleValidate() {
  console.log(`✅ [VALIDATE] Validation de la réponse pour le joueur ID=${DEFAULT_ID}...`);
  try {
    const res = await fetch(`${API_BASE}/validate/${DEFAULT_ID}`, { method: "POST" });
    const text = await res.text();
    console.log(`✅ [VALIDATE] Réponse du serveur : "${text}"`);
    await fetchScores();
  } catch (err) {
    console.error("❌ [VALIDATE] Erreur pendant la validation :", err);
    alert("Erreur pendant la validation");
  }
}

// ❌ Refus (mauvaise réponse)
async function handleBuzz() {
  console.log(`❌ [BUZZ] Refus du joueur ID=${DEFAULT_ID}...`);
  try {
    const res = await fetch(`${API_BASE}/buzz/${DEFAULT_ID}`, { method: "POST" });
    const text = await res.text();
    console.log(`✅ [BUZZ] Réponse du serveur : "${text}"`);
    await fetchScores();
  } catch (err) {
    console.error("❌ [BUZZ] Erreur pendant le refus :", err);
    alert("Erreur pendant le refus");
  }
}

// 🟢 Initialisation
console.log("🚀 [INIT] Chargement initial des scores...");
fetchScores();

// 🔁 Rafraîchissement régulier
setInterval(() => {
  console.log("⏱️ [AUTO-REFRESH] Rafraîchissement des scores...");
  fetchScores();
}, 2000);
