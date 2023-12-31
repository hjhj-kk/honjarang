import axios from 'axios';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PurchaseMap from '@/components/Market/Purchase/PurchaseMap';
import { API } from '@/apis/config';

export default function PurchaseCreate() {
  const [content, setContent] = useState('');
  const [deadline, setDeadline] = useState('');
  const [targetPersonCount, setTargetPersonCount] = useState('');
  const [productName, setProductName] = useState('');
  const [price, setPrice] = useState('');
  const [deliveryCharge, setDeliveryCharge] = useState('');
  const [position, setPosition] = useState();
  const [placeName, setPlaceName] = useState();

  const navigate = useNavigate();
  const token = localStorage.getItem('access_token');
  const createPurchase = () => {
    const headers = {
      Authorization: `Bearer ${token}`,
    };
    const data = {
      content: content,
      deadline: deadline.slice(0, 10) + ' ' + deadline.slice(11, 16) + ':00',
      target_person_count: Number(targetPersonCount),
      product_name: productName,
      price: Number(price),
      delivery_charge: Number(deliveryCharge),
      place_keyword: placeName,
      latitude: Number(position.lat),
      longitude: Number(position.lng),
    };
    console.log(data);
    axios
      .post(`${API.PURCHASES}`, data, { headers })
      .then((res) => {
        console.log(res.data);
        navigate(`/market/purchasedetail/${res.data}`);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  // 목표 인원 10명까지
  const handleTargetPersonCount = (e) => {
    const count = e.target.value;
    const newCount = Math.min(count, 10);
    setTargetPersonCount(newCount);
  };

  return (
    <div className="border rounded-lg max-w-2xl mx-auto mt-10 pb-3 p-5 space-y-5 ">
      <div>
        <div className="text-lg mb-1">상품명</div>
        <input
          type="text"
          value={productName}
          onChange={(e) => setProductName(e.target.value)}
          className="border border-gray2 focus:outline-main2 h-8 p-1 w-60"
        />
      </div>
      <div>
        <div className="text-lg mb-1">상품 가격</div>
        <input
          type="number"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          className="border border-gray2 focus:outline-main2 h-8 p-1 w-60"
        />
      </div>
      <div>
        <div className="text-lg mb-1">배송비</div>
        <input
          type="number"
          value={deliveryCharge}
          onChange={(e) => setDeliveryCharge(e.target.value)}
          className="border border-gray2 focus:outline-main2 h-8 p-1 w-60"
        />
      </div>
      <div>
        <div className="flex flex-row mb-1">
          <p className="text-lg">목표 인원</p>
          <p className="ml-1 text-gray4 text-sm flex items-end mb-1">
            (최대 10명까지 입력 가능)
          </p>
        </div>
        <input
          type="number"
          value={targetPersonCount}
          // onChange={(e) => setTargetPersonCount(e.target.value)}
          onChange={handleTargetPersonCount}
          className="border border-gray2 focus:outline-main2 h-8 p-1 w-60"
        />
      </div>
      <div>
        <div className="flex flex-row mb-1">
          <p className="text-lg">마감 기한</p>
          <p className="ml-1 text-gray4 text-sm flex items-end mb-1">
            (최대 30일)
          </p>
        </div>
        <input
          type="datetime-local"
          value={deadline}
          onChange={(e) => setDeadline(e.target.value)}
          className="border border-gray2 focus:outline-main2 h-8 p-1 w-60"
        />
      </div>
      <div>
        <div className="text-lg mb-1">만남의 장소 (상품 수령지)</div>
        <PurchaseMap
          placeName={placeName}
          setPlaceName={setPlaceName}
          position={position}
          setPosition={setPosition}
        />
      </div>
      <div>
        <div className="text-lg mb-1">상품소개</div>
        <textarea
          className="border border-gray2 rounded p-2 w-full resize-none h-48 focus:outline-main2"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        ></textarea>
      </div>
      {/* <div>
        <div>상품사진 첨부</div>
        <input type="file" />
      </div> */}
      <div>
        <button
          type="button"
          className="main1-full-button w-32"
          onClick={createPurchase}
        >
          작성완료
        </button>
        <p className="text-gray3 text-xs mt-2">
          모집글 생성시 보증금 1,000포인트가 차감됩니다.
        </p>
      </div>
    </div>
  );
}
