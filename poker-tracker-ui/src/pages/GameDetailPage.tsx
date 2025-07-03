import { useParams } from "react-router-dom";
import type { Game, Player } from "../types/poker";
import { useEffect, useState } from "react";
import { addPlayer, getGameById, getPlayers } from "../api/gameApi";

export default function GameDetailPage() {
    const { id } = useParams();
    const [game, setGame] = useState<Game | null>(null);
    const [players, setPlayers] = useState<Player[]>([]);
    const [newPlayerName, setNewPlayerName] = useState("");

    console.log("Game id from route:", id);

    useEffect(() => {
        if (id) {
            getGameById(id).then(setGame);
            getPlayers(id).then(setPlayers);
        }
    }, [id]);

    const handleAddPlayer = async () => {
        if (!newPlayerName && !newPlayerName.trim()) {
            alert("Player name must not be empty.");
            return;
        }

        const player = await addPlayer(id!, newPlayerName);

        setPlayers((prev) => [...prev, player]);
        setNewPlayerName("");
    };

    if (!game) {
        return <div>Loading...</div>;
    }

    return (
        <div className="p-6 max-w-3xl mx-auto">
            <h1 className="text-3xl font-bold mb-2">{game.name}</h1>
            <p className="text-gray-500 mb-6">
                Created on: {new Date(game.dateCreated).toLocaleString()}
            </p>

            <div className="mb-6">
                <h2 className="text-xl font-semibold mb-2">Players</h2>
                <ul className="mb-4 space-y-1">
                    {players.map((player) => (
                        <li key={player.id} className="text-blue-700">
                            👤 {player.name}
                        </li>
                    ))}
                </ul>
                <div className="flex gap-2">
                    <input
                        className="border px-3 py-1 rounded"
                        value={newPlayerName}
                        onChange={(e) => setNewPlayerName(e.target.value)}
                        placeholder="Add new player"
                    />
                    <button
                        onClick={handleAddPlayer}
                        className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
                    >
                        ➕ Add
                    </button>
                </div>
            </div>
        </div>
    );
}
