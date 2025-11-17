import { useState } from 'react'

function MenuItem({ name, onClick, isSelected }: {name:string, onClick: any, isSelected: boolean}){
  return <button style={{
    width: '100%',
    textAlign: 'center',
    color: 'black',
    fontSize: '20px',
    border: 'none',
    padding: '8px 0',
    backgroundColor: isSelected? 'white': 'transparent'
  }} onClick={onClick}>
    {name}
  </button>
}


interface SidebarArg {
  title: string
  menuItems: string[]
}
function Sidebar({title, menuItems}: SidebarArg){
  // assert menuItems.length >= 1
  let [selected, set_selected] = useState(0)
  return (
    <nav style={{
      display: 'grid',
      gridTemplateRows: '180px 1fr 90px',
      width: '100%', height: '100%',
      backgroundColor: 'rgba(219, 168, 234, 0.45)'
    }}>
      <h3 style={{
        fontFamily: 'Times New Roman',
        fontSize: '40px',
        color: '#3788A7',
        width: '100%', height: '100%',
        textAlign: 'center'
      }}>{title}</h3>
      <div style={{
        display: 'flex',
        flexDirection: 'column',
        width: '100%'
      }}>
        {
          menuItems.map((itemName: string, i: number) => (
              <MenuItem
                name={itemName}
                isSelected={selected === i}
                onClick={() => {
                  if(selected === i) return
                  set_selected(i)
                }}
              />
            ))
        }
      </div>
      <button style={{
        width:'100%',
        backgroundColor: 'transparent',
        border: 'none'
      }}>로그아웃</button>
    </nav>
  )
}



export default function App() {
  return (
    // 해당 div가 최상위 컨테이너(AppPage)
    <div style={{
      display: 'grid',
      gridTemplateColumns: '0.2715fr 1fr',
      width: '1002px',
      height: '695px'
    }}>
      <Sidebar title="OO항공" menuItems={[
        "여행 패키지 목록",
        "예약 내역 확인",
        "추천 패키지"
      ]} />
      <div></div>
    </div>
  )
}
