export interface Game {
  id: string;
  name: string;
  dateCreated: string;
  finished: boolean;
}

export interface Player {
  id: string;
  name: string;
  game: Game;
}

export interface BuyIn {
  id: string;
  amount: number;
  timestamp: string;
  player: Player;
}

export interface Stack {
  id: string;
  amount: number;
  player: Player;
}

export interface Result {
  playerId: string;
  playerName: string;
  totalBuyIn: number;
  finalStack: number;
  netResult: number;
}

export interface Settlement {
  fromPlayerId: string;
  toPlayerId: string;
  amount: number;
}

export interface GameSummary {
  results: Result[];
  settlements: Settlement[];
}
