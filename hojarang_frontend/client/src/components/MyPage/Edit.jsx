import ImageInput from './ImageInput';
import { Link } from 'react-router-dom';


export default function Edit({ modalState, setModalState }) {
  const onClickCloseButton = () => {
    setModalState(!modalState);
  };

  return (
    <div className="relative border bg-white m-auto rounded-lg w-4/12">
      <div className="p-14">
        <div className="space-y-4">
          <div className="flex justify-between mb-4">
            <div className="font-bold">회원정보 수정</div>
            <button type="button" onClick={onClickCloseButton}>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={1.5}
                stroke="currentColor"
                className="w-6 h-6"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
          <ImageInput />
          <div className="flex justify-center">
            <button type="button" className="main1-button w-48">
              프로필 사진 변경하기
            </button>
          </div>
          <div className="flex justify-between">
            <div className="font-bold">이메일</div>
            <div className="flex space-x-2">
              <div>samsung@ssafy.com</div>
              <div className="w-24"></div>
            </div>
          </div>
          <div className="flex justify-between">
            <div className="font-bold">닉네임</div>
            <div className="flex space-x-2">
              <div>SSAFY01</div>
              <button type="button" className="main2-button w-24">
                변경하기
              </button>
            </div>
          </div>
          <div className="flex justify-between">
            <div className="font-bold">주소</div>
            <div className="flex space-x-2">
              <div>경북 구미시 3공단 3로 302</div>
              <button type="button" className="main2-button w-24">
                주소 변경
              </button>
            </div>
          </div>
        </div>
        <div className="mt-10 space-y-2">
          <div className="cursor-pointer text-xs text-main1">
            <Link to="/findpassword/changepassword">
              비밀번호 변경
            </Link>
          </div>
          <div className="cursor-pointer text-xs text-main5">회원 탈퇴</div>
        </div>
      </div>
    </div>
  );
}
