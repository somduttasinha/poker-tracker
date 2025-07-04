import React from "react";
import type { BuyIn, Game, Player, Result, Settlement } from "../types/poker";

export default function SettlementModal({
  results,
  settlements,
  players,
  onClose,
}: {
  results: Result[];
  settlements: Settlement[];
  players: Record<string, Player>;
  onClose: () => void;
}) {
  const getNameById = (id: string) => {
    console.log("player id to look for", id);
    const player = players[id];
    return player ? player.name : "Unknown";
  };
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-lg w-[90%] max-w-2xl">
        <h2 className="text-2xl font-bold mb-4">💰 Settlement Summary</h2>

        <h3 className="text-lg font-semibold mb-2">📊 Final Results</h3>
        <table className="w-full text-sm border mb-4">
          <thead className="bg-gray-100">
            <tr>
              <th className="px-3 py-2 text-left">Player</th>
              <th className="px-3 py-2 text-right">Buy-In</th>
              <th className="px-3 py-2 text-right">Stack</th>
              <th className="px-3 py-2 text-right">Net</th>
            </tr>
          </thead>
          <tbody>
            {results.map((r) => (
              <tr key={r.playerId} className="border-t">
                <td className="px-3 py-2">{r.playerName}</td>
                <td className="px-3 py-2 text-right">
                  {r.totalBuyIn.toFixed(2)}
                </td>
                <td className="px-3 py-2 text-right">
                  {r.finalStack.toFixed(2)}
                </td>
                <td
                  className={`px-3 py-2 text-right font-semibold {
                    r.netResult >= 0 ? "text-green-600" : "text-red-600"
                  }`}
                >
                  {r.netResult >= 0 ? "+" : "-"}
                  {Math.abs(r.netResult).toFixed(2)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <h3 className="text-lg font-semibold mb-2">🤝 Settlements</h3>
        {settlements.length === 0 ? (
          <p className="text-gray-600">
            No settlements required. Everyone's even!
          </p>
        ) : (
          <ul className="space-y-2">
            {settlements.map((s, i) => (
              <li key={i} className="text-blue-800">
                {getNameById(s.fromPlayerId)} ➡️ {getNameById(s.toPlayerId)}{" "}
                <strong>{s.amount.toFixed(2)}</strong>
              </li>
            ))}
          </ul>
        )}

        <div className="flex justify-end mt-6">
          <button
            onClick={onClose}
            className="bg-gray-300 hover:bg-gray-400 px-4 py-2 rounded"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
