import './styles.css';
import ProductImg from 'assets/images/products.png';
import ProductPrice from 'components/ProductPrice';

const ProductCard = () => {
  return (
    <>
      <div className="base-card product-card">
        <div className="product-image">
          <img src={ProductImg} alt="Imagem do produto" />
        </div>
        <div className="product-description">
          <h6>Computador Desktop - Intel Core i7</h6>
          <ProductPrice />
        </div>
      </div>
    </>
  );
};

export default ProductCard;
