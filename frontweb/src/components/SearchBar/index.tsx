import './styles.css';

const SearchBar = () => {
  return (
    <div className="product-search-bar">
      <div className="product-search-bar-title">
        <h3>Catálogo de produtos</h3>
      </div>

      <div className="base-card search-card">
        <div className="search">
          <p>Pesquisar produtos</p>
        </div>

        <div className="filter">
          <div className="filter-category">
            <p>Filtrar por categoria</p>
          </div>

          <div className="filter-clear">
            <b>LIMPAR FILTRO</b>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SearchBar;
