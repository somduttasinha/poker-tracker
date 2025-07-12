import { useParams } from "react-router-dom";
import type { BuyIn, Game, GameSummary, Player } from "../types/poker";
import { useEffect, useState } from "react";
import {
    getGameById,
    getAllBuyInsForGame,
    getPlayers,
    getGameSummary,
} from "../api/gameApi";
import { unicodeCards } from "../util/exports";
import SettlementModal from "../components/SettlementModal";

export default function FinishedGameDetailPage() {
    const { id } = useParams();
    const [game, setGame] = useState<Game | null>(null);
    const [players, setPlayers] = useState<Player[]>([]);
    const [allBuyIns, setAllBuyIns] = useState<BuyIn[]>([]);
    const [gameSummary, setGameSummary] = useState<GameSummary | null>(null);
    const [showGameSummary, setShowGameSummary] = useState(false);
    const [playerMap, setPlayerMap] = useState<Record<string, Player>>({});

    function getCardForPlayer(playerId: string): string {
        const hash = [...playerId].reduce((acc, c) => acc + c.charCodeAt(0), 0);
        return unicodeCards[hash % unicodeCards.length];
    }

    useEffect(() => {
        if (id) {
            getGameById(id).then(setGame);
            getPlayers(id).then(setPlayers);
        }
    }, [id]);

    useEffect(() => {
        if (players) {
            getAllBuyInsForGame(players).then(setAllBuyIns);
        }
    }, [players]);

    useEffect(() => {
        if (players) {
            // populate player map
            const playerMap: Record<string, Player> = {};
            players.forEach((player) => {
                playerMap[player.id] = player;
            });

            setPlayerMap(playerMap);
        }
    }, [players]);

    const buyInsByPlayer: Map<
        string,
        {
            player: Player;
            total: number;
        }
    > = allBuyIns.reduce((acc, buyIn) => {
        const playerId = buyIn.player.id;
        const existing = acc.get(playerId);

        if (existing) {
            existing.total += buyIn.amount;
            acc.set(playerId, existing);
            return acc;
        } else {
            acc.set(playerId, {
                player: buyIn.player,
                total: buyIn.amount,
            });
        }

        return acc;
    }, new Map<string, { player: Player; total: number }>());

    if (!game) {
        return <div>Loading...</div>;
    }

    const handleGetGameSummary = async (gameId: string) => {
        try {
            const summary = await getGameSummary(gameId);
            setGameSummary(summary);
            setShowGameSummary(true);
        } catch (err) {
            console.error("Failed to get game summary", err);
        }
    };

    return (
        <div className="p-6 max-w-6xl mx-auto flex flex-col lg:flex-row gap-10">
            {/* Left: Player & Buy-In Controls */}
            <div className="flex-1">
                <h1 className="text-4xl font-bold mb-2">🃏 {game.name}</h1>
                <p className="text-gray-500 mb-6">
                    📅 Created on:{" "}
                    <span className="italic">
                        {new Date(game.dateCreated).toLocaleString()}
                    </span>
                </p>

                {/* Players */}
                <div className="mb-8">
                    <h2 className="text-2xl font-semibold mb-2">👥 Players</h2>
                    <ul className="mb-4 space-y-1">
                        {players.map((player) => {
                            return (
                                <li key={player.id} className="flex items-center gap-2">
                                    <span className="text-xl">{getCardForPlayer(player.id)}</span>
                                    {player.name}
                                </li>
                            );
                        })}
                    </ul>
                </div>

                {/* Buy-In Form */}
                <div>
                    <h2 className="text-2xl font-semibold mb-2">💰 Buy-Ins</h2>

                    <ul className="space-y-1 text-gray-700">
                        {allBuyIns.map((b) => (
                            <li key={b.id}>
                                🪙 <span className="font-semibold">{b.player.name}</span> bought
                                in for <span className="text-green-700">{b.amount}</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            {/* Right: Total Buy-Ins Table */}
            <div className="lg:w-[320px] w-full bg-yellow-50 rounded-lg shadow p-4">
                <h3 className="text-xl font-bold mb-4">💳 Total Buy-Ins</h3>
                <table className="w-full text-sm border">
                    <thead>
                        <tr className="bg-yellow-200 text-left">
                            <th className="px-3 py-2">Player</th>
                            <th className="px-3 py-2">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.from(buyInsByPlayer.values())
                            .sort((a, b) => b.total - a.total)
                            .map(({ player, total }) => (
                                <tr key={player.id} className="border-t hover:bg-yellow-100">
                                    <td className="px-3 py-2 text-xl">
                                        {getCardForPlayer(player.id)} {player.name}
                                    </td>
                                    <td className="px-3 py-2 text-green-700 font-semibold">
                                        {total.toFixed(2)}
                                    </td>
                                </tr>
                            ))}
                    </tbody>
                </table>
            </div>

            {game.finished && (
                <button
                    className="flex items-center justify-center gap-2 px-6 py-3 rounded-lg bg-blue-600 text-white font-semibold hover:bg-blue-700 transition"
                    onClick={() => handleGetGameSummary(game.id!)}
                >
                    🧾 Settle Game
                </button>
            )}

            {showGameSummary && (
                <SettlementModal
                    results={gameSummary?.results || []}
                    settlements={gameSummary?.settlements || []}
                    players={playerMap}
                    onClose={() => setShowGameSummary(false)}
                />
            )}
        </div>
    );
}
