import axios from "axios";
import type { BuyIn, Game, GameSummary, Player } from "../types/poker";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export const getGames = async () => {
  const res = await api.get("/api/games");
  return res.data;
};

export const createGame = async (name: string) => {
  const res = await api.post("/api/games", { name });
  return res.data;
};

export const endGame = async (id: string) => {
  const res = await api.put(`/api/games/end/${id}`);
  return res.data;
};

export const getGameById = async (id: string) => {
  const res = await api.get<Game>(`/api/games/${id}`);
  return res.data;
};

export const getPlayers = async (gameId: string) => {
  const res = await api.get<Player[]>(`/api/games/${gameId}/players`);
  console.log("players", res.data);

  return res.data;
};

export const addPlayer = async (gameId: string, name: string) => {
  const res = await api.post<Player>(`/api/games/${gameId}/players`, { name });
  return res.data;
};

export const getBuyIns = async (playerId: string) => {
  const res = await api.get<BuyIn[]>(`/api/players/${playerId}/buyins`);
  return res.data;
};

export const getAllBuyInsForGame = async (players: Player[]) => {
  const buyIns: BuyIn[] = [];
  for (const player of players) {
    const playerBuyIns = await getBuyIns(player.id);
    buyIns.push(...playerBuyIns);
  }
  return buyIns;
};

export const addBuyIn = async (playerId: string, amount: number) => {
  const res = await api.post<BuyIn>(`/api/players/${playerId}/buyins`, amount, {
    headers: {
      "Content-Type": "application/json",
    },
  });
  return res.data;
};

export const submitFinalStack = async (playerId: string, amount: number) => {
  const res = await api.post(`/api/players/${playerId}/stack`, amount, {
    headers: {
      "Content-Type": "application/json",
    },
  });
  return res.data;
};

export const getGameSummary = async (gameId: string): Promise<GameSummary> => {
  const res = await api.get(`/api/games/${gameId}/summary`);
  return res.data;
};
