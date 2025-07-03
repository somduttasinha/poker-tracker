import { useEffect, useState } from "react";
import { getGames, createGame } from "../api/gameApi";
import { Link } from "react-router-dom";
import type { Game } from "../types/poker";

export default function HomePage() {
    const [games, setGames] = useState<Game[]>([]);
    const [name, setName] = useState("");

    useEffect(() => {
        getGames().then(setGames);
    }, []);

    const handleCreate = async () => {
        const trimmedName = name.trim();
        if (!trimmedName) {
            alert("Game name must not be empty.");
            return;
        }
        const game = await createGame(name);
        setGames((prev) => [...prev, game]);
        setName("");
    };

    return (
        <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl mx-auto bg-white p-6 rounded-xl shadow">
                <h1 className="text-4xl font-bold text-gray-800 mb-6 flex items-center gap-2">
                    <span role="img" aria-label="home">
                        👀
                    </span>
                    Current Poker Games
                </h1>

                <div className="flex flex-col sm:flex-row gap-4 mb-6">
                    <input
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Enter new game name"
                        className="flex-1 border border-gray-300 rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <button
                        onClick={handleCreate}
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
                    >
                        ➕ Create Game
                    </button>
                </div>

                {games.length === 0 ? (
                    <p className="text-gray-500">No games yet. Create one above!</p>
                ) : (
                    <ul className="space-y-2">
                        {games.map((game) => (
                            <li key={game.id}>
                                <Link
                                    to={`/games/${game.id}`}
                                    className="block border border-gray-300 rounded px-4 py-2 hover:bg-gray-50 transition text-blue-700 font-medium"
                                >
                                    🎲 {game.name}
                                </Link>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}
