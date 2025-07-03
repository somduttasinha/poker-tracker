import axios from "axios";
import type { Game, Player } from "../types/poker";

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

export const getGameById = async (id: string) => {
    const res = await api.get<Game>(`/api/games/${id}`);
    return res.data;
};

export const getPlayers = async (gameId: string) => {
    const res = await api.get<Player[]>(`/api/games/${gameId}/players`);
    return res.data;
};

export const addPlayer = async (gameId: string, name: string) => {
    const res = await api.post<Player>(`/api/games/${gameId}/players`, { name });
    return res.data;
};
