import { Link } from "react-router-dom";

export default function Layout({ children }: { children: React.ReactNode }) {
    return (
        <div>
            <nav>
                <Link to="/">🏠 Home</Link>
            </nav>
            <main>{children}</main>
        </div>
    );
}
