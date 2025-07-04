import { useParams } from "react-router-dom";
import type { BuyIn, Game, GameSummary, Player } from "../types/poker";
import { useEffect, useState } from "react";
import {
  addBuyIn,
  addPlayer,
  getBuyIns,
  getGameById,
  getAllBuyInsForGame,
  getPlayers,
  endGame,
  getGameSummary,
} from "../api/gameApi";
import { unicodeCards } from "../util/exports";
import FinaliseGameModal from "../components/FinaliseGameModal";
import SettlementModal from "../components/SettlementModal";

export default function GameDetailPage() {
  const { id } = useParams();
  const [game, setGame] = useState<Game | null>(null);
  const [players, setPlayers] = useState<Player[]>([]);
  const [newPlayerName, setNewPlayerName] = useState("");
  const [buyIns, setBuyIns] = useState<BuyIn[]>([]);
  const [amount, setAmount] = useState("");
  const [selectedPlayerId, setSelectedPlayerId] = useState<string>("");
  const [allBuyIns, setAllBuyIns] = useState<BuyIn[]>([]);
  const [numBuyIns, setNumBuyIns] = useState(0);
  const [showFinaliseModal, setShowFinaliseModal] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [gameSummary, setGameSummary] = useState<GameSummary | null>(null);
  const [showGameSummary, setShowGameSummary] = useState(false);
  const [playerMap, setPlayerMap] = useState<Record<string, Player>>({});

  function getCardForPlayer(playerId: string): string {
    const hash = [...playerId].reduce((acc, c) => acc + c.charCodeAt(0), 0);
    return unicodeCards[hash % unicodeCards.length];
  }

  useEffect(() => {
    if (id) {
      if (!selectedPlayerId) {
        // BuyIn array should just be empty
        setBuyIns([]);
      } else {
        getBuyIns(selectedPlayerId).then(setBuyIns);
      }
    }
  }, [selectedPlayerId]);

  const handleAddBuyIn = async () => {
    if (!selectedPlayerId || !amount) {
      alert("Player must be selected and amount must not be empty.");
      return;
    }

    if (isCompleted) {
      alert("Game is already completed.");
      return;
    }

    const newBuyIn = await addBuyIn(selectedPlayerId!, Number(amount));
    setBuyIns((prev) => [...prev, newBuyIn]);
    setNumBuyIns((prev) => prev + 1);
    setAmount("");
  };

  useEffect(() => {
    if (id) {
      getGameById(id).then(setGame);
      getPlayers(id).then(setPlayers);
    }
  }, [id]);

  useEffect(() => {
    if (game) {
      setIsCompleted(game.finished);
    }
  }, [game]);

  useEffect(() => {
    if (players) {
      getAllBuyInsForGame(players).then(setAllBuyIns);
    }
  }, [numBuyIns, players]);

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

  const handleEndGame = async (gameId: string) => {
    try {
      await endGame(gameId);
      const updatedGame = await getGameById(gameId);
      setGame(updatedGame);
      setIsCompleted(updatedGame.finished);
    } catch (err) {
      console.error("Failed to end game", err);
    }
  };

  const handleAddPlayer = async () => {
    if (!newPlayerName && !newPlayerName.trim()) {
      alert("Player name must not be empty.");
      return;
    }

    if (isCompleted) {
      alert("Game is already completed.");
      return;
    }

    const player = await addPlayer(id!, newPlayerName);

    setPlayers((prev) => [...prev, player]);
    setNewPlayerName("");
  };

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
          <div className="flex gap-2">
            <input
              className="border px-3 py-1 rounded w-full"
              value={newPlayerName}
              onChange={(e) => setNewPlayerName(e.target.value)}
              placeholder="Add new player"
            />
            <button
              disabled={isCompleted}
              onClick={handleAddPlayer}
              className="bg-green-500 text-white px-4 py-1 rounded hover:bg-green-600"
            >
              ➕ Add
            </button>
          </div>
        </div>

        {/* Buy-In Form */}
        <div>
          <h2 className="text-2xl font-semibold mb-2">💰 Buy-Ins</h2>

          <div className="flex flex-wrap gap-2 mb-4">
            <select
              value={selectedPlayerId || ""}
              onChange={(e) => setSelectedPlayerId(e.target.value)}
              className="border px-3 py-1 rounded"
            >
              <option value="">🎯 Select Player</option>
              {players.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))}
            </select>

            <input
              type="number"
              value={amount}
              min={1}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="Amount"
              className="border px-3 py-1 rounded w-32"
            />

            <button
              className="bg-green-600 text-white px-4 py-1 rounded hover:bg-green-700"
              onClick={handleAddBuyIn}
              disabled={!selectedPlayerId || !amount || isCompleted}
            >
              ➕ Add Buy-In
            </button>
          </div>

          <ul className="space-y-1 text-gray-700">
            {buyIns.map((b) => (
              <li key={b.id}>
                🪙 <span className="font-semibold">{b.player.name}</span> bought
                in for <span className="text-green-700">${b.amount}</span>
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
      <button
        onClick={() => setShowFinaliseModal(true)}
        disabled={game.finished}
        className="flex items-center justify-center gap-2 px-6 py-3 rounded-lg bg-red-600 text-white font-semibold hover:bg-red-700 transition"
      >
        🏁 Finish Game
      </button>
      {showFinaliseModal && (
        <FinaliseGameModal
          game={game}
          players={players}
          buyIns={allBuyIns}
          endGameHandler={handleEndGame}
          onClose={() => setShowFinaliseModal(false)}
        />
      )}
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
