import React, { useState, useRef, useEffect } from 'react';
import { useParams } from 'react-router';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { useNavigate } from 'react-router';
import axios from 'axios';
import Talks from '@/components/Chatting/Talks';
import { useSelector } from 'react-redux';

const Chat = ({chatId}) => {
  const [Nickname, setNickname] = useState('')
  const params = useParams();
  const Key = chatId;
  const navigate = useNavigate();
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);
  const [stomp, setStomp] = useState(null);

  const token = localStorage.getItem('access_token');

  const stompClientRef = useRef(null);

  const connect = () => {
    const serverAddress = 'https://honjarang.kro.kr/chat';
    const socket = new SockJS(serverAddress);
    const stompClient = Stomp.over(socket);

    stompClient.connect('guest', 'guest', (frame) => {
      stompClient.subscribe(`/topic/room.${Key}`, (message) => {
        console.log(message);
        showMessage(JSON.parse(message.body));
      });
      stompClient.send(`/app/chat/connect.${Key}`, {}, JSON.stringify({ token: token }));

      stompClientRef.current = stompClient;
      setSocket(socket);
      setStomp(stompClient);
    });
  };

  const showMessage = (message) => {
    console.log(message);
    setMessages((prevMessages) => {
      if (!prevMessages.includes(message)) {
        return [...prevMessages, message];
      }
      return prevMessages;
    });
  };

  const sendMessage = () => {
    const messageToSend = {
      room_id: Key,
      content: message,
      nickname: Nickname
    };

    stomp.send(`/app/chat/message.${Key}`, {}, JSON.stringify(messageToSend));
    setMessage('');
    
    // Scroll to bottom after sending message
    scrollToBottom();
  };

  const scrollToBottom = () => {
    const chatArea = document.getElementById('ChatArea');
    chatArea.scrollTop = chatArea.scrollHeight;
  };

  const handleBack = () => {
    if (stompClientRef.current) {
      console.log('Attempting to disconnect Stomp...');
      stompClientRef.current.disconnect(() => {
        console.log('Stomp disconnected.');
        if (socket) {
          socket.onclose = (event) => {
            console.log('Socket closed.', event);
            navigate('/chatting');
          };
          socket.close();
        } else {
          navigate('/chatting');
        }
      });
    } else if (socket) {
      console.log('Closing socket...');
      socket.onclose = (event) => {
        console.log('Socket closed.', event);
        navigate('/chatting');
      };
      socket.close();
    } else {
      navigate('/chatting');
    }
  };

  useEffect(() => {
    const cleanup = () => {
      if (stompClientRef.current) {
        console.log('Attempting to disconnect Stomp...');
        stompClientRef.current.disconnect(() => {
          console.log('Stomp disconnected.');
          if (socket) {
            socket.onclose = (event) => {
              console.log('Socket closed.', event);
              // 원하는 함수 실행
            };
            socket.close();
          } else {
            // 원하는 함수 실행
          }
        });
      } else if (socket) {
        console.log('Closing socket...');
        socket.onclose = (event) => {
          console.log('Socket closed.', event);
          // 원하는 함수 실행
        };
        socket.close();
      } else {
        // 원하는 함수 실행
      }
    };

    // 컴포넌트가 언마운트될 때 실행될 함수 등록
    return cleanup;
  }, []);


  useEffect(() => {
    connect();
  }, [chatId]);

  const onKeyEnter = (e) => {
    if (e.key === 'Enter') {
      sendMessage();
    }
  };

  useEffect(() => {
    const URL = import.meta.env.VITE_APP_API
    const id = localStorage.getItem('user_id')
    axios.get(`${URL}/api/v1/users/info`,
      {
        params : {id : id},
        headers : {'Authorization' : `Bearer ${token}`}
      },
      )
      .then(function(response){
        console.log(response.data)
        setNickname(response.data.nickname)
      })
      .catch(function(error){
        console.log(error)
      })
  },[]);

  return (
    <div className="w-3/5 h-screen flex flex-col m-auto">
      <Talks messages={messages} id={Key} Nickname = {Nickname}/>
      <div className="py-2 px-4 border-t border-gray-300 flex justify-around">
        <input type="text" id="message" value={message} onChange={(e) => setMessage(e.target.value)} className="border rounded p-2 w-10/12 focus:outline-main2" onKeyDown={onKeyEnter} />
        <button onClick={sendMessage} className="w-1/12 bg-main1 text-white rounded px-4 py-2 hover:bg-main1">전송</button>
      </div>
      <button onClick={handleBack} className="bg-red-500 text-white rounded px-4 py-2 mt-2 mx-4 self-center hover:bg-red-600">뒤로가기</button>
    </div>
  );
};

export default Chat;
