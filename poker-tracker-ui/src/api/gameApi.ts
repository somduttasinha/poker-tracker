import axios from "axios";
import type { BuyIn, Game, GameSummary, Player } from "../types/poker";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
});

export const getGames = async () => {
    const res = await api.get("/games");
    return res.data;
};

export const createGame = async (name: string) => {
    const res = await api.post("/games", { name });
    return res.data;
};

export const endGame = async (id: string) => {
    const res = await api.put(`/games/end/${id}`);
    return res.data;
};

export const getGameById = async (id: string) => {
    const res = await api.get<Game>(`/games/${id}`);
    return res.data;
};

export const getPlayers = async (gameId: string) => {
    const res = await api.get<Player[]>(`/games/${gameId}/players`);
    console.log("players", res.data);

    return res.data;
};

export const addPlayer = async (gameId: string, name: string) => {
    const res = await api.post<Player>(`/games/${gameId}/players`, { name });
    return res.data;
};

export const getBuyIns = async (playerId: string) => {
    const res = await api.get<BuyIn[]>(`/players/${playerId}/buyins`);
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
    const res = await api.post<BuyIn>(`/players/${playerId}/buyins`, amount, {
        headers: {
            "Content-Type": "application/json",
        },
    });
    return res.data;
};

export const submitFinalStack = async (playerId: string, amount: number) => {
    const res = await api.post(`/players/${playerId}/stack`, amount, {
        headers: {
            "Content-Type": "application/json",
        },
    });
    return res.data;
};

export const getGameSummary = async (gameId: string): Promise<GameSummary> => {
    const res = await api.get(`/games/${gameId}/summary`);
    return res.data;
};
