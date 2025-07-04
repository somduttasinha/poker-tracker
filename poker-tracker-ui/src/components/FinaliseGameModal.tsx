import React, { useState, useMemo } from "react";
import type { BuyIn, Game, Player } from "../types/poker";
import { endGame, submitFinalStack } from "../api/gameApi";

interface Props {
  game: Game;
  players: Player[];
  buyIns: BuyIn[];
  endGameHandler: (id: string) => void;
  onClose: () => void;
}

const FinaliseGameModal: React.FC<Props> = ({
  game,
  players,
  buyIns,
  endGameHandler,
  onClose,
}) => {
  const [finalStacks, setFinalStacks] = useState<Record<string, number>>({});
  const [error, setError] = useState<string | null>(null);

  console.log("buyIns", buyIns);

  // Calculate total buy-ins
  const totalBuyIn = useMemo(() => {
    return buyIns.reduce((sum, b) => sum + b.amount, 0);
  }, [buyIns]);

  // Calculate total stack submitted
  const totalStack = useMemo(() => {
    return Object.values(finalStacks).reduce((sum, amount) => sum + amount, 0);
  }, [finalStacks]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (totalBuyIn !== totalStack) {
      setError(
        `Total stack (${totalStack}) must equal total buy-in (${totalBuyIn})`,
      );
      return;
    }

    try {
      await Promise.all(
        Object.entries(finalStacks).map(([playerId, amount]) =>
          submitFinalStack(playerId, amount),
        ),
      );
      endGameHandler(game.id);
      onClose();
    } catch (err) {
      console.error("Error submitting stacks", err);
      setError("Something went wrong while submitting stacks.");
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-lg w-[90%] max-w-xl">
        <h2 className="text-xl font-bold mb-4">
          🏁 Finalize Game: {game.name}
        </h2>
        {error && <p className="text-red-600 mb-4">{error}</p>}

        <form onSubmit={handleSubmit}>
          {players.map((player) => (
            <div key={player.id} className="mb-3">
              <label className="block font-medium mb-1">
                {player.name}'s Final Stack
              </label>
              <input
                type="number"
                min="0"
                value={finalStacks[player.id] ?? ""}
                onChange={(e) =>
                  setFinalStacks((prev) => ({
                    ...prev,
                    [player.id]: Number(e.target.value),
                  }))
                }
                className="border px-3 py-1 rounded w-full"
                placeholder="Enter amount"
              />
            </div>
          ))}

          <div className="mt-2 text-sm text-gray-600">
            <p>💳 Total Buy-In: {totalBuyIn.toFixed(2)}</p>
            <p>📦 Total Stack: {totalStack.toFixed(2)}</p>
          </div>

          <div className="flex justify-end gap-2 mt-4">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-300 px-4 py-2 rounded hover:bg-gray-400"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
            >
              Submit Stacks
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default FinaliseGameModal;
