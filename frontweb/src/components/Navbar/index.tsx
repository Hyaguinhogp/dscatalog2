import './styles.css';
import 'bootstrap/js/src/collapse.js';

const Navbar = () => {
  return (
    <>
      <nav className="navbar navbar-expand-md navbar-dark bg-primary main-nav">
        <div className="container-fluid">
          <a href="link" className="nav-logo-text">
            <h4>DS Catalog</h4>
          </a>

          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#dscatalog-navbar"
            aria-controls="dscatalog-navbar"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>

          <div className="collapse navbar-collapse ds-navbar" id="dscatalog-navbar">
            <div className="space-1"></div>
            <div className="space-2">
              <ul className="navbar-nav main-menu">
                <li>
                  <a href="link">
                    HOME
                  </a>
                </li>
                <li>
                  <a href="link" className="active">CATÁLOGO</a>
                </li>
                <li>
                  <a href="link">ADMIN</a>
                </li>
              </ul>
            </div>
            <div className="space-3"></div>
          </div>
        </div>
      </nav>
    </>
  );
};

export default Navbar;
