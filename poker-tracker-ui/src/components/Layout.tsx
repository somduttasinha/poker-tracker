import { Link } from "react-router-dom";

export default function Layout({ children }: { children: React.ReactNode }) {
    return (
        <div className="min-h-screen bg-[#f9fafb] text-gray-800">
            <nav className="w-full px-6 py-4 bg-[#f9fafb] border-b border-gray-200 shadow-sm flex gap-4">
                <Link to="/" className="text-lg font-medium hover:underline">
                    🏠 Home
                </Link>
                <Link
                    to="/games/completed"
                    className="text-lg font-medium hover:underline"
                >
                    ✅ Completed
                </Link>
            </nav>
            <main className="p-6">{children}</main>
        </div>
    );
}
