import { useEffect, useState } from "react";
import { getFinishedGamesSince } from "../api/gameApi";
import { Link } from "react-router-dom";
import type { Game } from "../types/poker";

export default function FinishedGamesListPage() {
    const [games, setGames] = useState<Game[]>([]);

    useEffect(() => {
        // get finished game since 24h ago as a LocalDateTime
        getFinishedGamesSince(24).then(setGames);
    }, []);

    return (
        <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-2xl mx-auto bg-white p-6 rounded-xl shadow">
                <h1 className="text-4xl font-bold text-gray-800 mb-6 flex items-center gap-2">
                    <span role="img" aria-label="home">
                        ✅
                    </span>
                    Completed Poker Games
                </h1>

                {games.length === 0 ? (
                    <p className="text-gray-500">
                        No completed games in the last 24 hours!
                    </p>
                ) : (
                    <ul className="space-y-2">
                        {games.map((game) => (
                            <li key={game.id}>
                                <Link
                                    to={`/games/completed/${game.id}`}
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
