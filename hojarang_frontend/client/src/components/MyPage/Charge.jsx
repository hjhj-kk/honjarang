import {useState} from 'react'
import { Link } from 'react-router-dom';

export default function Charge() {
  const [activeRadio, setActiveRadio] = useState('5000');
  const handleClickRadioButton = (e) => {
    console.log(e.target.value);
    setActiveRadio(e.target.value);
  };
  const tabs = [
    {
      title: '5,000P',
      price: '5000',
    },
    {
      title: '10,000P',
      price: '10000',
    },
    {
      title: '30,000P',
      price: '30000',
    },
    {
      title: '50,000P',
      price: '50000',
    },
  ];
  return (
    <div className='space-y-4'>
      {tabs.map((tab, index) => {
        return (
          <div className="flex items-center m" key={index}>
            <input
              type="radio"
              className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500"
              value={tab.price}
              checked={activeRadio === `${tab.price}`}
              onChange={handleClickRadioButton}
            />
            <label
              htmlFor={`radio-${index}`}
              className="ml-2 text-sm font-medium text-gray-900 dark:text-gray-300"
            >
              {tab.title}
            </label>
          </div>
        );
      })}
      <hr />
      <div className='flex justify-between'>
        <div className='space-y-2'>
          <div className='text-xs'>현재 포인트</div>
          <div className='w-20 h-8 border-2 rounded-lg flex items-center text-xs'>5,000P</div>
        </div>
        <div>
          <div className='h-7'></div>
          <div>+</div>
        </div>
        <div className='space-y-2'>
          <div className='text-xs'>결제할 포인트</div>
          <div className='w-20 h-8 border-2 rounded-lg flex items-center text-xs'> {activeRadio}P </div>
        </div>
        <div>
          <div className='h-7'></div>
          <div>=</div>
        </div>
        <div className='space-y-2'>
          <div className='text-xs'>결제후 포인트</div>
          <div className='w-20 h-8 border-2 rounded-lg flex items-center text-xs'> 10,000P </div>
        </div>
      </div>
      <Link
        to="/checkout"
        state={{
          price: { activeRadio },
          nickName: '김싸피',
        }}
      >
        <button className="main1-button w-24 mt-20">결제하기</button>
      </Link>
    </div>
  )
}