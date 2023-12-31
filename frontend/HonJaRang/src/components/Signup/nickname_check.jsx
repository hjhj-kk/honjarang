import axios from 'axios';
import { useState, useEffect } from 'react';
import { API } from '@/apis/config';

export default function Nickname_check({
  Nickname,
  setNickname,
  ChangeNicknameValid,
}) {
  // 닉네임, 닉네임 오류 메시지
  const [nickname, setInput] = useState('');
  const [nicknameMsg, setnicknameMsg] = useState('');
  const [NicknameDisalbed, setNicknameDisalbed] = useState(false);

  const onChange = (event) => {
    setInput(event.target.value);
  };

  // 닉네임 유효성 검사(2자 ~ 15자, 한글,영어,숫자 포함 가능)
  let nicknameCheck = /^[A-Za-z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣]{2,15}$/;
  const nickname_check = () => {
    if (nicknameCheck.test(nickname) && nickname !== '탈퇴한 사용자') {
      axios
        .get(`${API.USER}/check-nickname`, { params: { nickname: nickname } })
        .then(function (response) {
          console.log(response);
          setnicknameMsg('사용 가능한 닉네임입니다.');
          setNickname(nickname);
          ChangeNicknameValid();
          setNicknameDisalbed(true);
        })
        .catch(function (error) {
          console.log(error);
          setnicknameMsg('중복된 닉네임입니다.');
        });
    } else {
      setnicknameMsg('사용할 수 없는 닉네임입니다.');
      console.log('닉네임 오류');
    }
  };

  return (
    <div className="mb-3 mt-2">
      <div>
        <label className="font-semibold text-lg text-main2">닉네임</label>
        <div>
          <input
            type="text"
            name="nickname"
            onChange={onChange}
            value={nickname}
            maxLength="15"
            disabled={NicknameDisalbed}
            className=" border-gray2 rounded-lg w-60 h-10 p-2 focus:outline-main2"
          />
          <button
            className="w-20 h-10 main4-full-button my-2 text-base ml-2"
            onClick={nickname_check}
            disabled={NicknameDisalbed}
          >
            중복 확인
          </button>
        </div>
        <span className="font-semibold text-xs text-main5">{nicknameMsg}</span>
      </div>
    </div>
  );
}
