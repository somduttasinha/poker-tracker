import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import GameDetailPage from "./pages/GameDetailPage";
import Layout from "./components/Layout";
import FinishedGamesListPage from "./pages/FinishedGamesListPage";
import FinishedGameDetailPage from "./pages/FinishedGameDetailPage";

function App() {
    return (
        <Router>
            <Layout>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/games/:id" element={<GameDetailPage />} />
                    <Route path="/games/completed/" element={<FinishedGamesListPage />} />
                    <Route
                        path="/games/completed/:id"
                        element={<FinishedGameDetailPage />}
                    />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;
