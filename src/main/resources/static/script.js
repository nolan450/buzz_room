const API_BASE = "http://localhost:8080";
let DEFAULT_ID = 0;
let countdownInterval = null;

// 🚀 SSE – écoute en temps réel
const sse = new EventSource(`${API_BASE}/sse/events`);

sse.onmessage = (event) => {
  console.log("📩 [SSE] Reçu :", event.data);

  const [type, payload] = event.data.split(":");

  switch (type) {
    case "buzz":
      const buzzId = parseInt(payload, 10);
      DEFAULT_ID = buzzId;

      document.querySelector(".status-text").innerText = `Joueur ${buzzId} a buzzé !`;
      document.querySelector(".icon-large").classList.remove("fa-spinner", "fa-pulse");
      document.querySelector(".icon-large").classList.add("fa-bolt");

      // Timer 10 secondes
      clearInterval(countdownInterval);
      let timeLeft = 10;
      const countdown = document.getElementById("countdown");
      countdown.innerText = `⏳ Temps restant : ${timeLeft}s`;

      countdownInterval = setInterval(() => {
        timeLeft--;
        countdown.innerText = `⏳ Temps restant : ${timeLeft}s`;

        if (timeLeft <= 0) {
          resetVisualState();
        }
      }, 1000);
      break;

    case "valid":
      console.log(`✅ Joueur ${payload} a donné une bonne réponse.`);
      const playerLi = document.getElementById(`player-${payload}`);
      if (playerLi) {
        // Met à jour le score manuellement (+1)
        let current = parseInt(playerLi.querySelector("span").innerText);
        playerLi.querySelector("span").innerText = `${current + 1} pts`;
      }
      break;


    case "register":
      console.log(`🧾 Nombre de joueurs ${payload} enregistré`);

      // Convertit le nombre reçu
      let nb_joueur = parseInt(payload, 10);

      // Réinitialise la liste
      const list = document.getElementById("scoreList");
      list.innerHTML = "";

      // Ajoute tous les joueurs de 1 à n avec score 0
      for (let i = 1; i <= nb_joueur; i++) {
        const li = document.createElement("li");
        li.id = `player-${i}`;
        li.innerHTML = `Joueur ${i} <span>0 pts</span>`;
        list.appendChild(li);
      }
      break;
    default:
      console.log("⚠️ Type SSE inconnu :", type);
  }
};

async function handleCorrectAnswer() {
  try {
    const res = await fetch(`${API_BASE}/api/validate/${DEFAULT_ID}`, { method: "POST" });
    const text = await res.text();
    console.log(`[VALIDATE] ${text}`);
    resetVisualState();
  } catch (err) {
    console.error("[VALIDATE] Erreur :", err);
  }
}

async function handleWrongAnswer() {
  try {
    const res = await fetch(`${API_BASE}/api/penalize/${DEFAULT_ID}`, { method: "POST" });
    const text = await res.text();
    console.log(`[PENALIZE] ${text}`);
    resetVisualState();
  } catch (err) {
    console.error("[PENALIZE] Erreur :", err);
  }
}

function resetVisualState() {
  clearInterval(countdownInterval);
  document.getElementById("statusText").innerText = "En attente d'une réponse...";
  document.getElementById("buzz-icon").className = "fas fa-bell icon-large";
  document.getElementById("countdown").innerText = "";
}



// Démarrage
fetchScores();