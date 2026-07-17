import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button, Card, Form, Nav, Alert } from "react-bootstrap";
import { setAuthState } from "../../../shared/utils/auth.js";
import api from "../../../shared/services/axios.js";
import "./AuthPages.css";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Vui lòng nhập đầy đủ email và mật khẩu.");
      return;
    }
    setError("");
    setLoading(true);

    try {
      const response = await api.post("/auth/login", { email, password });
      const { accessToken, role, email: resEmail } = response.data;
      
      setAuthState({ accessToken, role, email: resEmail });
      
      // Redirect to cart page after login
      navigate("/cart");
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || 
        "Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản và mật khẩu."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <Card className="auth-card">
        <Nav className="auth-tabs">
          <Nav.Item>
            <Nav.Link as={Link} to="/login" className="auth-tab active">
              ĐĂNG NHẬP
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link as={Link} to="/register" className="auth-tab">
              ĐĂNG KÝ
            </Nav.Link>
          </Nav.Item>
        </Nav>

        <Card.Body className="auth-body">
          {error && <Alert variant="danger" className="rounded-0 small py-2">{error}</Alert>}
          <Form onSubmit={handleLogin}>
            <Form.Control
              className="auth-field"
              type="email"
              placeholder="Nhập email"
              autoComplete="username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={loading}
              required
            />

            <Form.Control
              className="auth-field"
              type="password"
              placeholder="Mật khẩu"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
              required
            />

            <div className="auth-actions">
              <Button type="submit" className="auth-primary-btn login" disabled={loading}>
                {loading ? "ĐANG ĐĂNG NHẬP..." : "ĐĂNG NHẬP"}
              </Button>
            </div>

            <div className="auth-center-link">
              <a href="#">Quên mật khẩu?</a>
            </div>

            <div className="auth-divider">Hoặc đăng nhập với</div>

            <div className="auth-social-row">
              <Button type="button" className="auth-social-btn facebook" disabled={loading}>
                <span className="icon">f</span>
                <span className="label">Đăng nhập bằng Facebook</span>
              </Button>

              <Button type="button" className="auth-social-btn google" disabled={loading}>
                <span className="icon">G</span>
                <span className="label">Đăng nhập bằng Google</span>
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
}

